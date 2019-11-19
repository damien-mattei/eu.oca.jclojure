
;; lein new eu.oca.jclojure

(defproject eu.oca.jclojure "0.1.0-SNAPSHOT"
  :description "library clojure JVM source code for Sidonie web interface administration"
  :url "https://sidonie.oca.eu"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
	    :dependencies [[org.clojure/clojure "1.8.0"]
	                   [mysql/mysql-connector-java "5.1.38"]
                           [org.clojure/java.jdbc "0.7.3"]
  		           [hiccup "1.0.5"]
                           [rm-hull/infix "0.3.3"]
                           [clj-time "0.15.2"]  ]
  :aot :all
  :main eu.oca.jclojure
 )
