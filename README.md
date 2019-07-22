# Login Server
### How to

a. Using docker
    
    - sudo docker build -t login/login .
    
    - sudo docker run -p 8070:8070 -d login/login
    
b. Maven
    
    - mvn spring-boot:run

c. Properties

    - application.properties: # profile to use
    
    - application-xxxx.properties: # properties loaded depending on the profile used
    
d. Frontend
    
    - Profile to use: dev
    - default root url access: http://localhost:8070
    
e. Default credentials

    - username: admin / password: test 
    - username: user / password: user 
    