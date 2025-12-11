import json
import boto3
import urllib.request

# Initialize Lambda client
lambda_client = boto3.client('lambda')

# Lambda function for new recommendations
NEW_RECOMM_LAMBDA_NAME = "RecommendationServiceLambda"

# EC2 legacy base URL (without endpoint)
EC2_BASE_URL = "http://34.204.196.116:8080"

def lambda_handler(event, context):
    """
    Lambda Router: routes requests dynamically for old and new users
    """
    try:
        # Get userId from query params
        user_id = event.get("queryStringParameters", {}).get("userId", "new_user")

        if user_id.startswith("old"):
            # Full legacy routing to EC2
            source = "EC2"

            # Call /recommendations
            with urllib.request.urlopen(f"{EC2_BASE_URL}/recommendations") as response:
                rec_result = response.read().decode()
            try:
                recommendations = json.loads(rec_result)
            except json.JSONDecodeError:
                recommendations = [item.strip() for item in rec_result.strip('[]').split(',')]

            # Call /search
            with urllib.request.urlopen(f"{EC2_BASE_URL}/search") as response:
                search_result = response.read().decode()
            try:
                search_results = json.loads(search_result)
            except json.JSONDecodeError:
                search_results = [item.strip() for item in search_result.strip('[]').split(',')]

            result = {
                "userId": user_id,
                "recommendations": recommendations,
                "searchResults": search_results,
                "source": source
            }

        else:
            # Strangler fig: Lambda recommendation + EC2 search
            source = "Hybrid"

            # Lambda recommendation
            payload = json.dumps({"userId": user_id})
            response = lambda_client.invoke(
                FunctionName=NEW_RECOMM_LAMBDA_NAME,
                InvocationType='RequestResponse',
                Payload=payload
            )
            result_payload = response['Payload'].read().decode()
            try:
                recommendations = json.loads(result_payload)
            except json.JSONDecodeError:
                recommendations = result_payload

            # EC2 search
            with urllib.request.urlopen(f"{EC2_BASE_URL}/search") as response:
                search_result = response.read().decode()
            try:
                search_results = json.loads(search_result)
            except json.JSONDecodeError:
                search_results = [item.strip() for item in search_result.strip('[]').split(',')]

            result = {
                "userId": user_id,
                "recommendations": recommendations,
                "searchResults": search_results,
                "source": source
            }

        return {
            "statusCode": 200,
            "body": json.dumps(result),
            "headers": {
                "Content-Type": "application/json"
            }
        }

    except Exception as e:
        return {
            "statusCode": 500,
            "body": json.dumps({"error": str(e)})
        }
