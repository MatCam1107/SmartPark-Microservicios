import json

def lambda_handler(event, context):
    for record in event["Records"]:
        body = json.loads(record["body"])

        entry = {
            "accion": "MENSAJE_RECIBIDO",
            "messageId": record["messageId"],
            "pruebaId": body["pruebaId"],
            "evento": body["evento"],
            "asignacionId": body["asignacionId"]
        }

        print(json.dumps(entry), flush=True)