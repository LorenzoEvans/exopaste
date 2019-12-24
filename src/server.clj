(ns exopaste.server
    (:require [bidi.ring :refer [make-handler]]
              [com.stuartsierra.component :as component]
              [aleph.http :as http]
              [ring.util.request :as req]
              [ring.util.response :as res]
              [ring.middleware.params :refer [wrap-params]]
              [exopaste.view :as view]
              [exopaste.store :as store]))

(defrecord HttpServer [server]
; This component wraps the aleph webserver from http/start-server,
; and we pass it the result of calling handler that builds the bidi route 
; handler, which maps urls to functions that produce the associated content
    component/Lifecycle
    (start [this]
        (assoc this :server (http/start-server (handler (:store this)) {:port 8080})))
    (stop [this]
        (assoc this :server nil)))

(defn make-server []
    (map->HttpServer {}))

(defn paste-handler [store request]
    (let [paste (store/get-paste-by-uuid store (:uuid (:route-params request)))]
        (res/response (view/render-paste paste))))
        
(defn handler [store]
; this will return the result of the index-handler and paste-handler functions to our
; http client, when launched via the system map.
; we use partial because ring will only allow handlers a single parameter, which is the request
; the application of partial binds the store to the handler, returning a (? the same) function that
; takes a single argument.
    (make-handler ["/" {"" (partial index-handler store)
                        [:uuid] (partial paste-handler store)}])) ; partial allows for request & store.
