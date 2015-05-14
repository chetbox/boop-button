(ns boop.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [resource-response content-type]]))

(def boops (atom 0))

(defroutes app

  (GET "/" []
    (content-type (resource-response "index.html")
                  "text/html"))

  (GET "/api/boops" []
    (str @boops))

  (POST "/api/boop" []
    (str (swap! boops inc)))

  (route/not-found "Not Found"))
