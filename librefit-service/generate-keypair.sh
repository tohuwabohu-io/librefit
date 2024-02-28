echo "Generating keypair..."
openssl req -newkey rsa:2048 -new -nodes -keyout privateKey.pem -out csr.pem 2>/dev/null
openssl rsa -in privateKey.pem -pubout > publicKey.pem 2>/dev/null
cp privateKey.pem build/resources/main/
cp publicKey.pem build/resources/main/
mv privateKey.pem src/main/resources/
mv publicKey.pem src/main/resources/
x=$(ls -l src/main/resources/*.pem | wc -l)
echo "... finished. Counting $x generated files."

if [ "$x" -lt 2 ]
  then
    echo "Less than expected files created."
    exit 1
fi