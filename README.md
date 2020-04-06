# Open Liberty - Eclipse MicroProfile Demo - The Void

Thanks for checking out the repo, this was a rough draft project trying to play with RH ServiceMesh as well as create a backend application that had a couple different transactions with different services. We utilized Eclipse MicroProfile to create the backend in the Open Liberty Framework to create a RESTful experience with JAX-RS. It connects to MongoDB and Redis for session key caching. Yaml files are provided to deploy everything to a cluster. A docker file is also provided if you'd prefer to build a local image, however the file `k8s_yamls/backend.yaml` provides a public image readily available if you check the template image field.

##  How to Run locally
- You will first need a couple env variables set. 
  - DB_HOST="\<host of where mongo service is>"
  - DB_PORT="\<port exposed for DB service, (default 27017)>"
  - R_HOST="\<host of where Redis service is>"
  - R_PORT="\<port of redis (default 6379)>"
  - DB_ADMINUSER="\<you choose admin username for db>"
  - DB_ADMINPWD="\<you choose admin password for db>"
 - Set up your Mongo Service
   - Run `docker run -p $DB_PORT:27017 -d -e MONGO_INITDB_ROOT_USERNAME=$DB_ADMINUSER -e MONGO_INITDB_ROOT_PASSWORD=DB_ADMINPWD mongo:3.6.7 --auth`
  - Next, setup redis service
    - `docker run -p $R_PORT:6379 -d redis:alpine`
   - Then start your backend, in root folder of project, please run:
     - `mvn liberty:dev` or `mvn liberty:run`
 - Then navigate to `localhost:9080` in a browser. If you see a calculator, it's running.
 - Next use post man or another request generator to query the different endpoints found in `endpoints.xlsx`
## If running on a cluster

Just login into your cluster then `kubectl apply -f k8s_yamls/*.yaml` or `oc apply -f k8s_yamls/*.yaml`. If there are environment variables to change please change `configmap.yaml` data fields or edit the `adminscret.yaml` for DB_ADMINUSER and DB_ADMINPWD.

## Link to a working Frontend if needed
- [backend](https://github.com/mary-crivelli/void-frontend)
