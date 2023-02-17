npm install
npm run generate-api
npm run build
npm link
cd ../librefit-web
npm link ../librefit-api
npm install ../librefit-api --package-lock-only
npm run build
