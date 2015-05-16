(ns boop.fb-auth
  (:require [boop.db :as db]
            [clj-oauth2.client :as oauth2]
            [clj-oauth2.ring :as oauth2-ring]
            [clojure.data.json :as json]))


; Session management

(def user-sessions (atom {})) ; TODO: make cache with time limit

(defn- register-session!
  [session-id user-id]
  (swap! user-sessions
         assoc session-id user-id)
  val)

(defn- new-session?
  [session-id]
  (not (contains? @user-sessions session-id)))


; Facebook OAuth2

(defn fb-oauth2
  [config]
  (merge
    {:authorization-uri "https://graph.facebook.com/oauth/authorize"
     :access-token-uri "https://graph.facebook.com/oauth/access_token"
     :redirect-uri "supplied by config"
     :client-id "supplied by config"
     :client-secret "supplied by config"
     :access-query-param :access_token
     :scope ["email"]
     :grant-type "authorization_code"
     :get-state oauth2-ring/get-state-from-session
     :put-state oauth2-ring/put-state-in-session
     :get-target oauth2-ring/get-target-from-session
     :put-target oauth2-ring/put-target-in-session
     :get-oauth2-data oauth2-ring/get-oauth2-data-from-session
     :put-oauth2-data oauth2-ring/put-oauth2-data-in-session}
   config))

(defn- get-data
  [fb-oauth2-resp url]
  (println "Fetching" url "...")
  (let [resp (oauth2/get url {:oauth2 fb-oauth2-resp})]
    (if (= 200 (:status resp))
      (json/read-str (:body resp))
      (throw (Exception. (format "Unable to fetch data (HTTP status: %d): %s"
                                 (:status resp)
                                 (:body resp)))))))


(defn get-user-info
  [fb-oauth2-resp]
  (get-data fb-oauth2-resp "https://graph.facebook.com/me"))


(defn- wrap-register-user-session
  [handler]
  (fn [req]
    (let [session-id (:session/key req)]
      (when (new-session? session-id)
        (register-session! session-id
                           (db/get-or-create-user-id (get-user-info (:oauth2 req)))))
      (handler (assoc req
                 :user-id (@user-sessions session-id))))))

(defn wrap-fb-auth
  [handler config]
  (-> handler
      (wrap-register-user-session)
      (oauth2-ring/wrap-oauth2 (fb-oauth2 (:facebook-login config)))))
