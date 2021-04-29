(ns broker.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defroutes app-routes
  (POST "/brokers" [] "Hello World")
  (POST "/brokers/:broker-id/quotes" [] "Hello World")
  (POST "/brokers/:broker-id/policies" [] "Hello World")
  (GET  "/brokers/:broker-id/policies/:policy-id" [broker-id policy-id] (str "Hello World" broker-id policy-id))
  (route/not-found "Not Found"))

(def app
  (wrap-defaults app-routes site-defaults))
