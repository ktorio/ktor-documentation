curl -X GET --location "http://127.0.0.1:8080/order" \
    -H "Content-Type: application/json"

curl -X GET --location "http://127.0.0.1:8080/order/2020-04-06-01" \
    -H "Content-Type: application/json"

curl -X GET --location "http://127.0.0.1:8080/order/2020-04-06-01/total" \
    -H "Content-Type: application/json"