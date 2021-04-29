(ns broker.handler
  (:require [compojure.core :refer :all]
            [clojure.pprint :as pp]
            [compojure.route :as route]
            [ring.util.response :refer [response]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]))

(defprotocol BrokerRepository
  (create-broker [this broker] "creates a broker")
  (get-broker [this uuid] "fetches a broker"))

(def broker-db (atom {}))
(defrecord MockBrokerRepository []
  BrokerRepository
  (create-broker [this broker]
    (let [uuid (.toString (java.util.UUID/randomUUID))
          id-broker (assoc broker :uuid uuid)]
         (swap! broker-db assoc uuid id-broker)
         id-broker))
  (get-broker [this uuid]
    (get @broker-db uuid)))

(defn get-broker-handler [req]
  (println @broker-db)
  (println (get-in req [:params :uuid]))
  (->
    (get-broker (MockBrokerRepository.) (get-in req [:params :uuid]))
    (response)))

(defn create-broker-handler [req]
  (let
      [broker (-> req :body (select-keys ["first_name" "last_name"]))
       result (create-broker (MockBrokerRepository. ) broker)]
      (response result)))

(defroutes app-routes
  (POST "/brokers" [] create-broker-handler)
  (GET "/brokers/:uuid" [] get-broker-handler)
  (POST "/brokers/:broker-id/quotes" [] "Hello World")
  (POST "/brokers/:broker-id/policies" [] "Hello World")
  (GET  "/brokers/:broker-id/policies/:policy-id" [broker-id policy-id] (str "Hello World" broker-id policy-id))
  (route/not-found "Not Found"))

(def app
  (->
   app-routes
   wrap-json-body
   wrap-json-response
   (wrap-defaults (assoc-in site-defaults [ :security :anti-forgery ] false))))
