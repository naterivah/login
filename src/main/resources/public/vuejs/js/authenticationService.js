class AuthenticationService {

    constructor(errorCallback= (error)=> {}){
        this.errorCallback = errorCallback;
    }

    async authenticatedRequest(requestHandler=(http)=> {}){
        let http = this.createAxiosInstance(X_AUTH_TOKEN_HEADER, AuthenticationService.getTokenFromLocalStorage());
        return requestHandler(http);
    }
    async login(headerKey,headerValue){
        try {
            let http = this.createAxiosInstance(headerKey, headerValue);
            let response = await http.post(PATH_USER_INFO, {}, {});
            if (headerKey !== X_AUTH_TOKEN_HEADER){
                let token = response.headers[X_AUTH_TOKEN_HEADER];
                localStorage.setItem(X_AUTH_TOKEN_HEADER, token);
            }
            return {message: null, user: response.data};
        } catch (e) {
            console.log('error: ', e);
            return {message:{text:e.message, status: STATUS_ERROR}, user: null};
        }
    }
    async loginWithBasicAuthorizationHeader (username,password) {
        return await this.login(AUTHORIZATION_HEADER,AuthenticationService.basicAuthenticationHeader(username,password));
    }
    async loginWithTokenFromLocalStorage () {
        let token = AuthenticationService.getTokenFromLocalStorage();
        if (token) {
            return await this.login(X_AUTH_TOKEN_HEADER,token);
        } else {
            console.log("token not present");
            return {};
        }
    }

    createAxiosInstance (headerKey, headerValue) {
        let headers = {
            [headerKey]: headerValue,
            'Content-Type': 'application/json',
            'X-Requested-With': 'XMLHttpRequest'
        };
        let http = axios.create({
            baseURL: REST_URL,
            headers: headers
        });
        http.interceptors.response.use(response => {return response}, error => {
            return this.errorCallback(error);
        });
        return http;
    }

    static getTokenFromLocalStorage () {
        return localStorage.getItem(X_AUTH_TOKEN_HEADER);
    }
    static hasRole(user,expectedRole) {
        let authorities = user.authorities || [ROLE_ANONYMOUS];
        for(let authority of authorities) {
            if(authority === expectedRole) {
                return true;
            }
        }
        return false;
    }
    static basicAuthenticationHeader(username,password) {
        return 'Basic ' + window.btoa(username + ':' + password);
    }
}
