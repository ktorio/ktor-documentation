curl -X POST --location "http://127.0.0.1:8080/customer" \
    -H "Content-Type: application/json" \
    -d "{
          \"id\": \"100\",
          \"firstName\": \"Jane\",
          \"lastName\": \"Smith\",
          \"email\": \"jane.smith@company.com\"
        }"

curl -X POST --location "http://127.0.0.1:8080/customer" \
    -H "Content-Type: application/json" \
    -d "{
          \"id\": \"200\",
          \"firstName\": \"John\",
          \"lastName\": \"Smith\",
          \"email\": \"john.smith@company.com\"
        }"

curl -X POST --location "http://127.0.0.1:8080/customer" \
    -H "Content-Type: application/json" \
    -d "{
          \"id\": \"300\",
          \"firstName\": \"Mary\",
          \"lastName\": \"Smith\",
          \"email\": \"mary.smith@company.com\"
        }"

curl -X GET --location "http://127.0.0.1:8080/customer" \
    -H "Accept: application/json"

curl -X GET --location "http://127.0.0.1:8080/customer/200" \
    -H "Accept: application/json"

curl -X GET --location "http://127.0.0.1:8080/customer/500" \
    -H "Accept: application/json"

curl -X DELETE --location "http://127.0.0.1:8080/customer/100"

curl -X DELETE --location "http://127.0.0.1:8080/customer/500"