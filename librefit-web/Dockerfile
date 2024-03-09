FROM node:21-alpine
# AS builder
WORKDIR /app
COPY . .
RUN rm .env
RUN npm prune --production

# FROM node:18-alpine
# WORKDIR /app
# COPY --from=builder /app/build build/
# COPY --from=builder /app/node_modules node_modules/
# COPY package.json .
EXPOSE 3000
ENV NODE_ENV=production
CMD [ "npm", "run", "start" ]