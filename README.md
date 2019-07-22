# Stateless Authentication & Proxy Server (JWT)

Base for an authentication & secure proxy server implemented with spring boot, jwt and zuul.

Proxy feature:

- add your own url to proxy under the application.yml file. 
- you can then secure them by adding your own rules in the SecurityConfig class

### How to

a. Using docker
    
    - sudo docker build -t login/login .
    
    - sudo docker run -p 8070:8070 -d login/login
    
b. Maven
    
    - mvn spring-boot:run

c. Properties

    - other properties: https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html

    - application.yml: # profile to use
    
    - application-xxxx.yml: # properties loaded depending on the profile used
    
d. Frontend
    
    - Profile to use: dev
    - Simple implementation using VueJs. code located to src/main/resources/public
    - default root url access: http://localhost:8070
    
e. Default credentials

    - username: admin / password: test 
    - username: user / password: user 
    
    
a. Proxy
    
    - proxy is configured under the application-route.yml file
    
    - example of routing:
    
    zuul:
      routes:
        # example of route to proxy - add yours here
        hello:
          path: /admin/hello/**
          url: http://localhost:8585/greeting/nordine # example of route to proxy
    
f. Database

    - default is H2 (embedded)

g. Last release
    
    0.0.4 - 22/07/2019
