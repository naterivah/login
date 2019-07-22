const APP = new Vue({
    el: APP_ID,
    data: {
        username: null,
        password: null,
        user: null,
        init: false,
        message: null,
        authenticationService: null
    },
    mounted: async function () {
        this.authenticationService = new AuthenticationService(async (error) => {
            if (error.response.status === 401) {
                console.log("401");
                this.logout();
            }
            return Promise.reject(error);
        });
        let resp = await this.authenticationService.loginWithTokenFromLocalStorage();
        this.user = resp.user;
        this.message = resp.message;
        this.init = true;
    },
    methods: {
        adminPage: async function () {
            this.authenticationService.authenticatedRequest(async http => {
                let response = await http.get(PATH_ADMIN_WELCOME, {});
                this.message = {text: JSON.stringify(response.data,null,'\t'), status: STATUS_INFO}
            });
        },
        proxyPage: async function () {
            this.authenticationService.authenticatedRequest(async http => {
                let response = await http.get(PATH_PROXY_HELLO, {});
                this.message = {text: JSON.stringify(response.data,null,'\t'), status: STATUS_INFO}
            });
        },
        userPage: async function () {
            this.authenticationService.authenticatedRequest(async http => {
                let response = await http.post(PATH_USER_INFO, {}, {});
                this.message = {text: JSON.stringify(response.data,null,'\t'), status: STATUS_INFO}
            });
        },
        hasRole: function (expectedRole) {
            return AuthenticationService.hasRole(this.user, expectedRole);
        },
        clearForm: function () {
            this.username = null;
            this.password = null;
        },
        logout: function () {
            AuthenticationService.logout();
            this.user = null;
            this.clearForm();
            this.message = {text: 'Disconnected', status: STATUS_ERROR};
        },
        loginWithBasicAuthorizationHeader: async function (event) {
            if (event) {
                event.preventDefault();
            }
            let resp = await this.authenticationService.loginWithBasicAuthorizationHeader(this.username, this.password);
            this.user = resp.user;
            this.message = resp.message;
        }
    }
});
