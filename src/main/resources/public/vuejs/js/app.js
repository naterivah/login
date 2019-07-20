// *************************************************************************************************************
// Author: Nordine Bittich
// June 2019
// *************************************************************************************************************
const restUrl = location.protocol + '//' + location.hostname + ':' + location.port;

let app = new Vue({
    el: '#app',
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
    beforeMount: async function () {
        console.log('before mount');
    },
    methods: {
        adminPage: async function(){
            this.message="loading...";
            let http = this.createAxiosInstance("x-auth-token", this.getTokenFromLocalStorage());
            let response = await http.get("/admin/welcome",{});
            this.message = {text:response.data, status: "info"}
        },
        userPage: async function(){
            this.message="loading...";
            let http = this.createAxiosInstance("x-auth-token", this.getTokenFromLocalStorage());
            let response = await http.post("/user/info",{},{});
            this.message = {text:response.data, status:"info"}
        },
        hasRole: function(expectedRole) {
            let authorities = this.user.authorities || [ 'ANONYMOUS'];
            for(let authority of authorities) {
                console.log(authority);
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
        createAxiosInstance: function (headerKey, headerValue) {
            let http = axios.create({
                baseURL: restUrl,
                headers: {
                    [headerKey]: headerValue,
                    'Content-Type': 'application/json'
                }
            });
            http.interceptors.response.use(response => {
                return response;
            }, error => {
                if (error.response.status === 401) {
                    console.log("401");
                    this.message = {};
                    this.message.text = "Session timeout or wrong user/password";
                    this.message.status="danger";
                    this.logout();
                }
                return Promise.reject(error);
            });
            return http;
        },
        logout: function () {
            this.message = null;
            this.user = null;
            this.clearForm();
        },
        getTokenFromLocalStorage: function () {
            return localStorage.getItem("x-auth-token");
        },
        loginWithTokenFromLocalStorage: async function () {
            this.message = null;
            let token = this.getTokenFromLocalStorage();
            if (token) {
                try {
                    let http = this.createAxiosInstance("x-auth-token", token);
                    let response = await http.post("/user/info", {}, {});
                    this.user = response.data;
                } catch (e) {
                    console.log("an unexpected error occurred", e);
                }
            } else {
                console.log("token not present");
            }
        },
        loginWithBasicAuthorizationHeader: async function () {
            this.message = null;
            let headerKey = 'Authorization';
            let headerValue = 'Basic ' + window.btoa(this.username + ':' + this.password);
            try {
                let http = this.createAxiosInstance(headerKey, headerValue);
                let response = await http.post("/user/info", {}, {});
                this.user = response.data;
                let token = response.headers['x-auth-token'];
                localStorage.setItem('x-auth-token', token);
            } catch (e) {
                alert("login failed: " + e.message);
            }
        }
    }
});
