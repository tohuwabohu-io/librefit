import {Configuration, type FetchParams, type Middleware, type RequestContext} from 'librefit-api/rest';
import { PUBLIC_API_BASE_PATH } from '$env/static/public';

export class APIMiddleware implements Middleware {
    pre(context: RequestContext): Promise<FetchParams | void> {
        console.log('hello from the middleware')

        return Promise.resolve()
    }
}

export const DEFAULT_CONFIG = new Configuration({
    basePath: PUBLIC_API_BASE_PATH,
    middleware: [ new APIMiddleware() ]
});
