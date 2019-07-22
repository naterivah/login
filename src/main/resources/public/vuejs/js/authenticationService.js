class AuthenticationService {

    constructor(errorCallback= (error)=> {}){
        this.errorCallback = errorCallback;
    }

    async authenticatedRequest(requestHandler=(http)=> {}){
        let http = this.createAxiosInstance(AUTHORIZATION_HEADER, AuthenticationService.getTokenFromLocalStorage(), true);
        return requestHandler(http);
    }
    async login(headerKey,headerValue, jwt=false){
        try {
            let http = this.createAxiosInstance(headerKey, headerValue);
            let response = await http.post(PATH_USER_INFO, {}, {});
            if (!jwt){
                let token = response.headers[AUTHORIZATION_HEADER.toLowerCase()];
                localStorage.setItem(JWT_TOKEN_STORE, token);
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
            return await this.login(AUTHORIZATION_HEADER,token, true);
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
    static logout () {
        localStorage.removeItem(JWT_TOKEN_STORE);
    }
    static getTokenFromLocalStorage () {
        return localStorage.getItem(JWT_TOKEN_STORE);
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
