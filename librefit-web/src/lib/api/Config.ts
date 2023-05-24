import {Configuration, type FetchParams, type Middleware, type RequestContext} from 'librefit-api/rest';
import { PUBLIC_API_BASE_PATH } from '$env/static/public';

export const AUTH_TOKEN_KEY = 'AUTH_TOKEN';

export class JWTMiddleware implements Middleware {
    pre(context: RequestContext): Promise<FetchParams | void> {
        console.log('hello from the middleware');

        const token = localStorage.getItem(AUTH_TOKEN_KEY);

        if (token) {
            context.init.headers = {
                'Authorization': `Bearer ${token}`
            }
        }

        return Promise.resolve({
            url: context.url,
            init: context.init
        });
    }
}

export const DEFAULT_CONFIG = new Configuration({
    basePath: PUBLIC_API_BASE_PATH
});

export const JWT_CONFIG = new Configuration({
    basePath: PUBLIC_API_BASE_PATH,
    middleware: [ new JWTMiddleware() ]
});
