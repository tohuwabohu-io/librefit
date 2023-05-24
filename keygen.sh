openssl req -newkey rsa:2048 -new -nodes -keyout privateKey.pem -out csr.pem
openssl rsa -in privateKey.pem -pubout > publicKey.pem
mv privateKey.pem librefit-service/src/main/resources/
mv publicKey.pem librefit-service/src/main/resources/
rm csr.pem
