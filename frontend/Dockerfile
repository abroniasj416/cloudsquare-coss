FROM node:20-alpine AS build
WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .

ARG VITE_API_BASE_URL=/
ARG VITE_KEYCLOAK_URL=https://auth.hwangsoojin.cloud
ARG VITE_KEYCLOAK_REALM=coss
ARG VITE_KEYCLOAK_CLIENT_ID=coss-frontend
ARG VITE_KEYCLOAK_REGISTER_URL=https://auth.hwangsoojin.cloud/realms/coss/protocol/openid-connect/registrations?client_id=coss-frontend&response_type=code&scope=openid&redirect_uri=https://hwangsoojin.cloud

ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
ENV VITE_KEYCLOAK_URL=$VITE_KEYCLOAK_URL
ENV VITE_KEYCLOAK_REALM=$VITE_KEYCLOAK_REALM
ENV VITE_KEYCLOAK_CLIENT_ID=$VITE_KEYCLOAK_CLIENT_ID
ENV VITE_KEYCLOAK_REGISTER_URL=$VITE_KEYCLOAK_REGISTER_URL

RUN npm run build

FROM nginx:1.27-alpine

ENV WAS_UPSTREAM=coss-was-lb:8081

COPY nginx/default.conf.template /etc/nginx/templates/default.conf.template
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
