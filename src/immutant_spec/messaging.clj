(ns immutant-spec.messaging
  "A Queue/Topic spec is a map of at least the :name of the queue, but also any options you want to specify
   (e.g) {:name \"topic.foo\", :tx false}
  Included is a shadow of the basic messaging api for use with queue specs."
  (:import (org.immutant.messaging Destinationizer))
  (:require [immutant.registry :as registry]
            [immutant.messaging.hornetq :as hq]
            [immutant.messaging :refer :all]))

(defn- ^Destinationizer get-destinationizer
  []
  (registry/get "destinationizer"))

(defn exists?
  "Finds whether the named queue/topic exists"
  [name]
  (contains? (.getDestinations  (get-destinationizer)) (str name)))

(def start-opts (:valid-options (meta #'hq/set-address-options)))

(defn create
  "Creates the queue if it doesn't already exist"
  [spec]
  (let [name (str (:name spec))]
    (when-not (exists? name)
      (apply start
             name
             (apply concat (select-keys spec start-opts))))
    name))


(defn get-name
  "Gets the queue name for the given spec - if it already exists (either as a value in the key under :created, or globally) - it is returned."
  [spec]
  (or (:created spec)
      (create spec)))


(defmacro with-queue
  "Binds a symbol to a queue-spec, all operations performed within the body using the symbol as the queue-spec will
   not repeatedly look up the queue. It will also re-use its connection across operations"
  [binding & body]
  (let [[sym v] binding]
    `(let [v# ~v
           ~sym (assoc v# :created (get-name v#))]
       (with-connection {}
                        ~@body))))


(def stop-opts (:valid-options (meta #'stop)))

(defn stop!
  "Stops the topic/queue given the spec
   Options can be supplied, they will be merged with options in the spec itself."
  ([spec]
   (stop! spec nil))
  ([spec opts]
   (apply stop (get-name spec)
          (apply concat (select-keys (merge spec opts) stop-opts)))))

(def listen-opts (:valid-options (meta #'listen)))

(defn listen!
  "Adds a listener to the topic/queue given the spec
   Options can be supplied, they will be merged with options in the spec itself."
  ([spec f]
   (listen! spec f nil))
  ([spec f opts]
   (apply listen (get-name spec) f
           (apply concat (select-keys (merge spec opts) listen-opts)))))

(def publish-opts (:valid-options (meta #'publish)))

(defn publish!
  "Publishes a message to the topic/queue given the spec.
   Options can be supplied, they will be merged with options in the spec itself."
  ([spec msg]
   (publish! spec msg nil))
  ([spec msg opts]
   (apply publish (get-name spec) msg
            (apply concat (select-keys (merge spec opts) publish-opts)))))


