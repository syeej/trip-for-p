# FE Build
FROM node:18 AS build
WORKDIR /app
COPY ./package*.json .
RUN npm install

COPY . .
ENV NODE_ENV=development
RUN npm run build

# FE Run
FROM nginx:latest
COPY --from=build /app/dist /var/www/html/dist
EXPOSE 80

ENTRYPOINT ["nginx", "-g", "daemon off;"]
