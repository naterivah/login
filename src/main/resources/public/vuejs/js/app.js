// *************************************************************************************************************
// Author: Nordine Bittich
// *************************************************************************************************************
const REST_URL= location.protocol + '//' + location.hostname + ':' + location.port;
const APP_ID = '#app';
const X_AUTH_TOKEN_HEADER = 'x-auth-token';
const AUTHORIZATION_HEADER = 'Authorization';
const PATH_USER_INFO = '/user/info';
const PATH_ADMIN_WELCOME = '/admin/welcome';
const STATUS_INFO = 'info';
const STATUS_ERROR = 'danger';
const ROLE_ANONYMOUS = 'ANONYMOUS';

const APP = new Vue({
    el: APP_ID,
    data: {
        username: null,
        password: null,
        user: null,
        init:false,
        message:null
    },
    mounted: async function () {
        await this.loginWithTokenFromLocalStorage();
        this.init = true;
    },
    methods: {
        adminPage: async function(){
            let http = this.createAxiosInstance(X_AUTH_TOKEN_HEADER, this.getTokenFromLocalStorage());
            let response = await http.get(PATH_ADMIN_WELCOME,{});
            this.message = {text:response.data, status: STATUS_INFO}
        },
        userPage: async function(){
            let http = this.createAxiosInstance(X_AUTH_TOKEN_HEADER, this.getTokenFromLocalStorage());
            let response = await http.post(PATH_USER_INFO,{},{});
            this.message = {text:response.data, status:STATUS_INFO}
        },
        hasRole: function(expectedRole) {
            let authorities = this.user.authorities || [ROLE_ANONYMOUS];
            for(let authority of authorities) {
                if(authority === expectedRole) {
                    return true;
                }
            }
            return false;
        },
        clearForm: function () {
            this.username = null;
            this.password = null;
        },
        logout: function () {
            localStorage.removeItem(X_AUTH_TOKEN_HEADER);
            this.user = null;
            this.clearForm();
            this.message = {text:'Disconnected',status:STATUS_ERROR};
        },
        getTokenFromLocalStorage: function () {
            return localStorage.getItem(X_AUTH_TOKEN_HEADER);
        },
        loginWithTokenFromLocalStorage: async function () {
            let token = this.getTokenFromLocalStorage();
            if (token) {
                await this.login(X_AUTH_TOKEN_HEADER,token);
            } else {
                console.log("token not present");
            }
        },
        loginWithBasicAuthorizationHeader: async function () {
            let headerValue = 'Basic ' + window.btoa(this.username + ':' + this.password);
            await this.login(AUTHORIZATION_HEADER,headerValue);
        },
        login: async function(headerKey,headerValue){
            try {
                this.message = null;
                let http = this.createAxiosInstance(headerKey, headerValue);
                let response = await http.post(PATH_USER_INFO, {}, {});
                this.user = response.data;
                if (headerKey !== X_AUTH_TOKEN_HEADER){
                    let token = response.headers[X_AUTH_TOKEN_HEADER];
                    localStorage.setItem(X_AUTH_TOKEN_HEADER, token);
                }
            } catch (e) {
                this.message = {text:e.message, status: STATUS_ERROR};
            }
        },
        createAxiosInstance: function (headerKey, headerValue) {
            let headers = {
                [headerKey]: headerValue,
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            };
            let http = axios.create({
                baseURL: REST_URL,
                headers: headers
            });
            http.interceptors.response.use(response => response, error => {
                if (error.response.status === 401) {
                    console.log("401");
                    this.logout();
                }
                return Promise.reject(error);
            });
            return http;
        }
    }
});
