(ns boop.db
  (:require [taoensso.faraday :refer [get-item
                                      put-item
                                      update-item
                                      ensure-table]]))

(def users-table :boop-users)
(def boops-table :boops)

(def connection (atom nil))

(defn set-connection!
  [connection-info]
  (reset! connection connection-info)
  (ensure-table connection-info
                users-table
                [:id :s]
                {:throughput {:read 1
                              :write 1}
                 :block? true})
  (ensure-table connection-info
                boops-table
                [:boop-user-id :s]
                {:throughput {:read 10
                              :write 10}
                 :block? true}))


;; Users

(defn- put-user!
  [user-id credentials]
  (let [item (assoc credentials
               :boop-user-id user-id)]
    (put-item @connection
              users-table
              item)
    item))

(defn- get-user-from-credentials
  [credentials]
  (get-item @connection
            users-table
            {:id (get credentials "id")}))

(defn- new-user-id
  []
  (str (java.util.UUID/randomUUID)))

(defn get-or-create-user-id
  [credentials]
  (:boop-user-id
    (if-let [user (get-user-from-credentials credentials)]
      user
      (put-user! (new-user-id)
                 credentials))))

;; Counters

(defn get-counter
  [user]
  (get-item @connection
            boops-table
            {:boop-user-id user}))

(defn inc-counter!
  [user]
  (update-item @connection
               boops-table
               {:boop-user-id user}
               {:count [:add 1]}
               {:return :all-new}))

(defn reset-counter!
  [user label]
  (update-item @connection
               boops-table
               {:boop-user-id user}
               {:count [:put 0]
                :label [:put label]}
               {:return :all-new}))
