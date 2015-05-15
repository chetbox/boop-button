(ns boop.db
  (:require [taoensso.faraday :refer [get-item
                                      update-item
                                      ensure-table]]))

(def db-table :boop)

(def connection (atom nil))

(defn set-connection!
  [connection-info]
  (reset! connection connection-info)
  (ensure-table connection-info
                db-table
                [:user-id :s]
                {:throughput {:read 1
                              :write 1}
                 :block? true}))

(defn get-counter
  [user]
  (get-item @connection
            db-table
            {:user-id user}))

(defn inc-counter!
  [user]
  (update-item @connection
               db-table
               {:user-id user}
               {:count [:add 1]}
               {:return :all-new}))

(defn reset-counter!
  [user label]
  (update-item @connection
               db-table
               {:user-id user}
               {:count [:put 0]
                :label [:put label]}
               {:return :all-new}))
