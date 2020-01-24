;; Sidonie Database update application

;; Author: Damien MATTEI

;; method from:
;; https://stackoverflow.com/questions/2181774/calling-clojure-from-java

;; compile with:
;;  cd ~/Dropbox/eu.oca.jclojure/
;; [mattei@moita eu.oca.jclojure]$ lein uberjar

 
(ns eu.oca.jclojure
    (:gen-class
     :name eu.oca.jclojure
     ;; functions accessible from extern Java code
     :methods [  
               #^{:static true} [binomial [int int] double]
               #^{:static true} [banner [String] String]
               #^{:static true} [testLog [String] String]
               #^{:static true} [InterfaceNomResourceClojure [String] String]
               #^{:static true} [UpdateDBResourceClojure [String String String String String String String String String String String String String String String String] String]
               ]
     )
    (:require [clojure.java.jdbc :as jdbc]
              [clojure.string]
              [clojure.stacktrace]
              [infix.macros :refer [infix]]
              [clj-time.core :as time])
    
    (:use [hiccup.core] ;; core,form not needed
          [hiccup.page]
          [hiccup.form]
          ;;[eu.oca.jclojure.sidoniecommon]
          ))


;;(load-file "../sidonie-admin2/src/sidonie_admin2/sidonie-common.clj")
;;(load "link-sidonie-common")
(load "symbolic-link-sidonie-common")
;;(load "sidoniecommon2")
;;(load "../../../../sidonie-admin2/src/sidonie_admin2/sidonie-common")



(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


(defn binomial
  "Calculate the binomial coefficient."
  [n k]
  (let [a (inc n)]
    (loop [b 1
           c 1]
      (if (> b k)
        c
        (recur (inc b) (* (/ (- a b) b) c))))))

(defn -binomial
  "A Java-callable wrapper around the 'binomial' function."
  [n k]
  (binomial n k))

(declare readSigles)
(declare readObject)

(defn -main []
  (println (str "(binomial 5 3): " (binomial 5 3)))
  (println (str "(binomial 10042 111): " (binomial 10042 111)))
  (println (readObject "COU 123")))


(defn banner
  ([msg] (str "Hello " msg " !")))

(defn -banner
  "A Java-callable wrapper around the 'banner' function."
  [msg]
  (banner msg))


(defn -testLog
  "A Java-callable function."
  [msg]
  (do
    (readSigles)
    (banner msg)))



;; Right Ascension

;; this function could be use with Sidonie and WDS for alpha values 

;; The  hours, minutes, and tenths of minutes of Right 
;; Ascension for 2000, followed by the degrees and minutes of
;; Declination for 2000, with + and - indicating north and
;; south declinations.

;; Alpha 2000
(defn hhmmmGet-hh [hhmmm]
  ;;(int (/ hhmmm 1000))
  (int
   (infix
    hhmmm / 1000
    )))

(defn hhmmmGet-mmDOTm [hhmmm]
  (let [hh (hhmmmGet-hh hhmmm)
        mmm ;(- hhmmm (* hh 1000))
        (infix
         hhmmm - hh * 1000
         )
        mmDOTm (/ mmm 10.0)
        ]
    (print "jclojure.clj :  hhmmmGet-mmDOTm : mmDOTm = ")
    (println mmDOTm)
    mmDOTm))





;; Delta 2000
;; this function could be use with Sidonie and WDS for delta values
(defn ddmmGet-dd [ddmm]
  (int (/ ddmm 100)))


;; note: we get the absolute value for the minutes the sign is used only with degree
(defn ddmmGet-mm [ddmm]
  (let [dd (ddmmGet-dd ddmm)]
    ;;(- ddmm (* dd 100))
    (Math/abs
     (int
      (infix
       
       ddmm - dd * 100

       )))))






;; eu.oca.jclojure=> (def rs (readObject "COU 123")) 
;; Reading Coordonnées...
;; #'eu.oca.jclojure/rs
;; eu.oca.jclojure=> (:delta_2000 (despace (first rs)))
;; 2248.0
;; eu.oca.jclojure=> (despace (first rs))
;; {:n°type 57.0, :nom "COU 123", :n°_hip "*", :n°_ads nil, :orb "*", :n°_fiche 5770, :date_de_saisie #inst "1996-03-20T23:00:00.000000000-00:00", :spectre "K0", :delta_2000 2248.0, :mag2 "08.9", :nomsidonie "COU 123", :mag1 "08.6", :nom_opérateur "Andrée", :n°_bd "22.3963", :alpha_2000 20123.0, :modif #inst "2002-07-17T22:00:00.000000000-00:00"}
;; eu.oca.jclojure=> (despace0 (first rs))
;; {:n°type 57.0, :nom "COU 123", :n°_hip "*", :n°_ads nil, :orb "*", :n°_fiche 5770, :date_de_saisie #inst "1996-03-20T23:00:00.000000000-00:00", :spectre "K0", :delta_2000 2248.0, :mag2 "08.9", :nomsidonie "COU 123", :mag1 "08.6", :nom_opérateur "Andrée", :n°_bd "22.3963", :alpha_2000 20123.0, :modif #inst "2002-07-17T22:00:00.000000000-00:00"}


;; remove spaces in accessor names
(defn despace [m] 
  (zipmap (map #(keyword (clojure.string/replace (name %) " " "_"))
               (keys m))
          (vals m)))

(defn despace0 [m] 
  (zipmap (map (fn [x] (keyword
                        (clojure.string/replace (name x)
                                                " "
                                                "_")))
               (keys m))
          (vals m)))


(defn create-html [body]
  (html5 {:lang "en"} [:head
                       [:meta {:charset "UTF-8"}]]
         [:body body]))

;; create the interface in HTML to modify Astro data
(defn create-html-body-OK [mag1 mag2 hh mmDOTm dd mm record Nom]
  [:body
   [:p {:style "margin-bottom: 0cm", :align "center"}
    [:br]]
   [:p {:style "margin-bottom: 0cm", :align "center"}
    [:font {:style "font-size: 15pt", :size "4"}
     [:font {:style "font-size: 18pt", :size "5"} "Modification des identifications d&#39;un syst&eacute;me binaire"]" "]]
   [:p {:style "margin-bottom: 0cm", :align "center"}
    [:br]]
   [:p {:style "margin-bottom: 0cm", :align "center"}
    [:br]]

   ;;[:form {:name "Form", :action "../jersey/UpdateDB" :method "POST"}
   
   ;;[:form {:name "Form", :action "http://localhost:8080/sidonie-admin2-0.1.0-SNAPSHOT-standalone/test" :method "POST"}
   ;;[:form {:name "Form", :action "http://localhost:8080/sidonie-admin2-0.1.0-SNAPSHOT-standalone/UpdateDB" :method "POST"}
   ;;[:form {:name "Form", :action "http://localhost:8080/sidonie-admin/UpdateDB" :method "POST"}
   [:form {:name "Form", :action "https://sidonie.oca.eu/sidonie-admin/UpdateDB" :method "POST"}

    [:table {:style "page-break-before: always; page-break-inside: avoid", :width "100%", :cellspacing "1", :cellpadding "4"}  
     [:colgroup 
      [:col {:width "64*"}]]
     [:tbody 
      [:tr {:valign "top"}  
       [:td {:style "border-top: 1px double #808080; border-bottom: 1px double #808080; border-left: 1px double #808080; border-right: none; padding-top: 0.1cm; padding-bottom: 0.1cm; padding-left: 0.1cm; padding-right: 0cm", :width "25%"}  
        [:div {:align "center"}  
         [:p 
          [:font {:color "#00cccc"} "N° de fiche "
           ;; Warning 'disabled' attribute and the form  will no more submit the value !!!!
           ;;[:input {:name "No_Fiche", :value (:n°_fiche record), :size "8", :style "width: 2cm; height: 0.93cm", :type "text", :disabled "true"}]]]]]
           ;;[:input {:name "No_Fiche", :value (:n°_fiche record), :size "8", :style "width: 2cm; height: 0.93cm", :type "text"}]]]]]
           [:input {:name "No_Fiche", :value (:n°_fiche record), :size "8", :style "width: 2cm; height: 0.93cm", :type "text" :readonly "true"}]]]]]
       [:td {:style "border-top: 1px double #808080; border-bottom: 1px double #808080; border-left: 1px double #808080; border-right: none; padding-top: 0.1cm; padding-bottom: 0.1cm; padding-left: 0.1cm; padding-right: 0cm", :width "25%"}  
        [:div {:align "center"}  
         [:p 
          [:font {:color "#00cccc"} "Nom " 
           [:input {:name "Nom", :value Nom, :size "11", :style "width: 2.62cm; height: 0.93cm", :type "text"}]]]]]
       
       [:td {:style "border-top: 1px double #808080; border-bottom: 1px double #808080; border-left: 1px double #808080; border-right: none; padding-top: 0.1cm; padding-bottom: 0.1cm; padding-left: 0.1cm; padding-right: 0cm", :width "25%"} 
        [:div {:align "center"} 
         [:p {:title "The  hours, minutes, and tenths of minutes of Right Ascension for 2000"}
          [:font {:color "#00cccc"} "Alpha 2000  " 
           [:input {:name "Alpha_2000_hh", :value hh , :size "2", :title "Hours: format hh", :type "text"}] " h  "
           [:input {:name "Alpha_2000_mmDOTm", :value mmDOTm, :size "3", :title "minutes, and tenths of minutes: format mm.m", :type "text"}] " min"]]]]
       
       [:td {:style "border-top: 1px double #808080; border-bottom: 1px double #808080; border-left: 1px double #808080; border-right: 1px double #808080; padding-top: 0.1cm; padding-bottom: 0.1cm; padding-left: 0.1cm; padding-right: 0cm", :width "25%"}
        [:div {:align "center"}
         [:p {:title "the degrees and minutes of Declination for 2000, with + and - indicating north and south declinations."}
          [:font {:color "#00cccc"} "Delta 2000  " 
           [:input {:name "Delta_2000_dd", :value dd, :size "2", :title "Degrees: format dd with optional +/- sign before", :type "text"}] " °  "
           [:input {:name "Delta_2000_mm", :value mm, :size "3", :title "minutes: format mm", :type "text"}] " '" ]]]]]
      
      [:tr {:valign "top"}  
       [:td {:style "border-top: none; border-bottom: 1px double #808080; border-left: 1px double #808080; border-right: none; padding-top: 0cm; padding-bottom: 0.1cm; padding-left: 0.1cm; padding-right: 0cm", :width "25%"}  
        [:div {:align "center"}  
         [:p 
          [:font {:color "#00cccc"} "N° BD " 
           [:input {:name "No_BD", :value (:n°_bd record), :size "7", :style "width: 2.25cm; height: 0.93cm", :type "text"}]]]]] 
       [:td {:style "border-top: none; border-bottom: 1px double #808080; border-left: 1px double #808080; border-right: none; padding-top: 0cm; padding-bottom: 0.1cm; padding-left: 0.1cm; padding-right: 0cm", :width "25%"}  
        [:div {:align "center"}  
         [:p 
          [:font {:color "#00cccc"} "N° ADS " 
           [:input {:name "No_ADS", :value (:n°_ads record), :size "5", :style "width: 1.25cm; height: 0.93cm", :type "text"}]]]]] 
       [:td {:style "border-top: none; border-bottom: 1px double #808080; border-left: 1px double #808080; border-right: none; padding-top: 0cm; padding-bottom: 0.1cm; padding-left: 0.1cm; padding-right: 0cm", :width "25%"}  
        [:div {:align "center"}  
         [:p 
          [:font {:color "#00cccc"} "N° HIP " 
           [:input {:name "No_HIP", :value (:n°_hip record), :size "5", :style "width: 1.25cm; height: 0.93cm", :type "text"}]]]]] 
       [:td {:style "border-top: none; border-bottom: 1px double #808080; border-left: 1px double #808080; border-right: 1px double #808080; padding-top: 0cm; padding-bottom: 0.1cm; padding-left: 0.1cm; padding-right: 0.1cm", :width "25%"}  
        [:div {:align "center"}  
         [:p 
          [:font {:color "#00cccc"} "Spectre " 
           [:input {:name "Spectre", :value (:spectre record), :size "5", :style "width: 1.25cm; height: 0.93cm", :type "text"}]]]]]] 
      [:tr {:valign "top"}  
       [:td {:style "border-top: none; border-bottom: 1px double #808080; border-left: 1px double #808080; border-right: none; padding-top: 0cm; padding-bottom: 0.1cm; padding-left: 0.1cm; padding-right: 0cm", :width "25%"}  
        [:div {:align "center"}  
         [:p 
          [:font {:color "#00cccc"} "mag 1 " 
           [:input {:name "mag1", :value mag1, :size "4", :style "width: 0.9cm; height: 0.93cm", :type "text"}]]]]] 
       [:td {:style "border-top: none; border-bottom: 1px double #808080; border-left: 1px double #808080; border-right: none; padding-top: 0cm; padding-bottom: 0.1cm; padding-left: 0.1cm; padding-right: 0cm", :width "25%"}  
        [:div {:align "center"}  
         [:p 
          [:font {:color "#00cccc"} "mag 2 " 
           [:input {:name "mag2", :value mag2, :size "3", :style "width: 0.9cm; height: 0.93cm", :type "text"}]]]]] 
       [:td {:style "border-top: none; border-bottom: 1px double #808080; border-left: 1px double #808080; border-right: none; padding-top: 0cm; padding-bottom: 0.1cm; padding-left: 0.1cm; padding-right: 0cm", :width "25%"}  
        [:div {:align "center"}  
         [:p 
          [:font {:color "#00cccc"} "Orbite ? " 
           [:input {:name "Orb", :value (:orb record), :size "3", :style "width: 0.9cm; height: 0.93cm", :type "text"}]]]]] 
       [:td {:style "border-top: none; border-bottom: 1px double #808080; border-left: 1px double #808080; border-right: 1px double #808080; padding-top: 0cm; padding-bottom: 0.1cm; padding-left: 0.1cm; padding-right: 0.1cm", :width "25%"}  
        [:div {:align "center"}  
         [:p 
          [:font {:color "#00cccc"} "N°Type " 
           [:input {:name "NoType", :value (:n°type record), :size "3", :style "width: 0.9cm; height: 0.93cm", :type "text"}]]]]]]]] 
    [:p 
     [:br]
     [:br]"\n"] 
    [:div {:align "left"}  
     [:p 
      [:input {:value "Reset", :style "width: 2.04cm; height: 0.91cm; font-family: 'Liberation Sans', sans-serif; font-size: 12pt", :type "reset"}]]]
    
    [:div {:align "center"}
     
     ;; [:p 
     ;;  [:font {:color "#cc0808"} "Admin code: " 
     ;;   [:input {:type "password", :style "width: 3cm; height: 0.93cm", :size "4", :value "", :name "adminCode"}]]]
     
     [:br]
     [:br]] 
    [:div {:align "right"}  
     [:p 
      [:input {:name "confirm", :value "1", :style "width: 2.32cm; height: 0.9cm; font-family: 'Liberation Sans', sans-serif; font-size: 12pt", :type "checkbox"}]"Confirm "]]
    [:div {:align "center"}
     [:p 
      [:input {:value "SUBMIT", :style "width: 4.83cm; height: 1.32cm; font-family: 'Liberation Sans', sans-serif; font-size: 12pt", :type "submit"}]]]
    ] ;; close FORM
   [:p {:align "left"} "Made with  " 
    [:a {:href "https://clojure.org"}
     [:img {:src "../adminDB/images/Clojure-Logo.png", :title "Clojure web site", :alt "Clojure", :width "198", :height "58"}]]]
   ] ;; close BODY
  )






;; alt-; comment region

;; eu.oca.jclojure=> record
;; {:n° ads nil, :n° hip "*", :n°type 57.0, :nom "COU 123", :delta 2000 2248.0, :orb "*", :n° bd "22.3963", :date de saisie #inst "1996-03-20T23:00:00.000000000-00:00", :n° fiche 5770, :spectre "K0", :alpha 2000 20123.0, :mag2 "08.9", :nom opérateur "Andrée", :nomsidonie "COU 123", :mag1 "08.6", :modif #inst "2002-07-17T22:00:00.000000000-00:00"}
;; eu.oca.jclojure=> (type record)
;; clojure.lang.PersistentHashMap
;; eu.oca.jclojure=> (keys record)
;; (:n° ads :n° hip :n°type :nom :delta 2000 :orb :n° bd :date de saisie :n° fiche :spectre :alpha 2000 :mag2 :nom opérateur :nomsidonie :mag1 :modif)
;; eu.oca.jclojure=> (vals record)
;; (nil "*" 57.0 "COU 123" 2248.0 "*" "22.3963" #inst "1996-03-20T23:00:00.000000000-00:00" 5770 "K0" 20123.0 "08.9" "Andrée" "COU 123" "08.6" #inst "2002-07-17T22:00:00.000000000-00:00")

;; eu.oca.jclojure=> (def record (despace0 (first rs)))
;; #'eu.oca.jclojure/record
;; eu.oca.jclojure=> (:n°_fiche record)
;; 5770
;; eu.oca.jclojure=> (type (:n°_fiche record))
;; java.lang.Integer
;; mais en fait l'integer apparait en String dans la page HTML (?!)


;; this one is call from the Java code
(defn InterfaceNomResourceClojure
  [Nom]
  (let [
        records (readObject Nom)
        record0 (first records)
        ]
    
    (if (nil? record0)
      
      (create-html (create-html-body-red-centered "Objet non trouvé dans la Base de Données."))
      
      (let [
            record (despace record0) ;; remove the space char in accessor names
            mag1 (:mag1 record)
            mag2 (:mag2 record)
            
            Alpha_2000 (:alpha_2000 record)
            Delta_2000 (:delta_2000 record)

            hh (hhmmmGet-hh Alpha_2000)
            mmDOTm (hhmmmGet-mmDOTm Alpha_2000)

            dd (ddmmGet-dd Delta_2000)
            mm (ddmmGet-mm Delta_2000)
                 
            body (create-html-body-OK mag1 mag2 hh mmDOTm dd mm record Nom)
            ] ;; close LET definitions
        
        (create-html body)))))


(defn -InterfaceNomResourceClojure
  "A Java-callable wrapper around the 'InterfaceNomResourceClojure' function."
  [msg]
  (InterfaceNomResourceClojure msg))






(defn updateNoBDobject
  "Update the table Coordonnées given No_Fiche and N° BD in parameter."
  [ No_Fiche No_BD ]
  
  (println "jclojure.clj : updateNoBDObject : updating Coordonnées...")

  (jdbc/update! db :Coordonnées
                ;; verifier la casse des keywords
                {
             
                 ;; when there is a space in column name of table i must use a call to keyword function
             
                 (keyword "N° BD") No_BD
             
                 }
                ;; verifier l'ordre des item suivant:
                ["`N° Fiche` = ?" No_Fiche]
                {:entities (jdbc/quoted \`)} ; or (jdbc/quoted :mysql)
                ) ;; Update
 
  )


(defn updateNoADSobject
  "Update the table Coordonnées given No_Fiche and N° ADS in parameter."
  [ No_Fiche No_ADS ]
  
  (println "jclojure.clj : updateNoADSObject : updating Coordonnées...")

  (jdbc/update! db :Coordonnées
                ;; verifier la casse des keywords
                {
                 (keyword "N° ADS") No_ADS
                 }
                ;; verifier l'ordre des item suivant:
                ["`N° Fiche` = ?" No_Fiche]
                {:entities (jdbc/quoted \`)} ; or (jdbc/quoted :mysql)
                ) ;; Update
 
  )


;; eu.oca.jclojure=> (updateAlpha2000object 5770 20123.1)
;; jclojure.clj : updateNoADSObject : updating Coordonnées...
;; (1)

(defn updateAlpha2000object
  "Update the table Coordonnées given No_Fiche and Alpha 2000 in parameter."
  [ No_Fiche Alpha_2000 ]
  
  (println "jclojure.clj : updateNoADSObject : updating Coordonnées...")

  (jdbc/update! db :Coordonnées
                ;; verifier la casse des keywords
                {
                 (keyword "Alpha 2000") Alpha_2000 ;; verifier le typage !
                 }
                ;; verifier l'ordre des item suivant:
                ["`N° Fiche` = ?" No_Fiche] ;; it's an Integer
                {:entities (jdbc/quoted \`)} ; or (jdbc/quoted :mysql)
                ) ;; Update
 
  )





(defn -UpdateDBResourceClojure

  "A Java-callable function that will launch object database update."

  ;;[ Nom No_Fiche Alpha_2000_hh Alpha_2000_mmDOTm Delta_2000_dd Delta_2000_mm No_BD No_ADS No_HIP Spectre mag1 mag2 Orb NoType adminCode confirm]
  [ Nom No_Fiche Alpha_2000_hh Alpha_2000_mmDOTm Delta_2000_dd Delta_2000_mm No_BD No_ADS No_HIP Spectre mag1 mag2 Orb NoType confirm]

  ;; (UpdateDBResourceClojure Nom No_Fiche Alpha_2000_hh Alpha_2000_mmDOTm Delta_2000_dd Delta_2000_mm No_BD No_ADS No_HIP Spectre mag1 mag2 Orb NoType adminCode confirm)
  
  (UpdateDBResourceClojure Nom No_Fiche Alpha_2000_hh Alpha_2000_mmDOTm Delta_2000_dd Delta_2000_mm No_BD No_ADS No_HIP Spectre mag1 mag2 Orb NoType confirm)
  )




;; eu.oca.jclojure=> (readSigles)
;; Hello, World!
;; {:sigle ApJ, :intitulé AstroPhysical Journal}
;; AstroPhysical Journal
;; (:sigle :intitulé)
;; (ApJ AstroPhysical Journal)

;; {:sigle ApJS, :intitulé AstroPhysical Journal - supplement}
;; AstroPhysical Journal - supplement
;; (:sigle :intitulé)
;; (ApJS AstroPhysical Journal - supplement)

;; {:sigle A&A, :intitulé Astronomy and Astrophysics}
;; Astronomy and Astrophysics
;; (:sigle :intitulé)
;; (A&A Astronomy and Astrophysics)

;; {:sigle A&AS, :intitulé Astronomy and Astrophysics - supplement series}
;; Astronomy and Astrophysics - supplement series
;; (:sigle :intitulé)
;; (A&AS Astronomy and Astrophysics - supplement series)
;;
;; ...
;;

;; {:sigle SDS, :intitulé South's catalogue of Double Stars}
;; South's catalogue of Double Stars
;; (:sigle :intitulé)
;; (SDS South's catalogue of Double Stars)

;; {:sigle T, :intitulé Telescope}
;; Telescope
;; (:sigle :intitulé)
;; (T Telescope)

;; {:sigle L, :intitulé Lunette}
;; Lunette
;; (:sigle :intitulé)
;; (L Lunette)

;; (nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil nil)

(defn readSigles
  "Read the table Sigles and display them."
  []
  
  (println "Hello, World!")
  
					;(jdbc/query db ["SELECT * FROM Sigles"])
  
  (jdbc/query db
              ["select * from Sigles"]
	      ;;{:row-fn println}
	      ;;{:row-fn (fn [ r ] (println (first (rest (first r)))))}
	      {:row-fn (fn [ r ] 
			   (do
			    (println r)
			    (println (first 
				      (rest
				       (first
					(rest r)))))
			    (println (keys r))
			    (println (vals r))))})

  ;(jdbc/with-connection db
;			(jdbc/with-query-results rows
;						 ["select * from Sigles"]
;						 (println rows)))

  ;;(jdbc/query db
	      ;;["select * from Coordonnées"]
	      ;;{:row-fn println}
	      ;;{:row-fn (fn [ r ] (println (first 
		;;			   (rest (rest (rest r))))))}
	      ;;)
 
  )




;; eu.oca.jclojure=> (def records (readObject "KR   58"))
;; jclojure.clj : readObject : Reading Coordonnées...
;; #'eu.oca.jclojure/records
;; eu.oca.jclojure=> records
;; ({:n° ads "15861", :n° hip "*", :n°type 78.0, :nom "KR   58", :delta 2000 5952.0, :orb "*", :n° bd "59.2508", :date de saisie #inst "1998-11-16T23:00:00.000000000-00:00", :n° fiche 12477, :spectre "-", :alpha 2000 22206.0, :mag2 "09.1", :nom opérateur "Andrée", :nomsidonie "kr 58", :mag1 "09.0", :modif #inst "2007-02-19T23:00:00.000000000-00:00"})
;; eu.oca.jclojure=> (def record0 (first records))
;; #'eu.oca.jclojure/record0
;; eu.oca.jclojure=> record0
;; {:n° ads "15861", :n° hip "*", :n°type 78.0, :nom "KR   58", :delta 2000 5952.0, :orb "*", :n° bd "59.2508", :date de saisie #inst "1998-11-16T23:00:00.000000000-00:00", :n° fiche 12477, :spectre "-", :alpha 2000 22206.0, :mag2 "09.1", :nom opérateur "Andrée", :nomsidonie "kr 58", :mag1 "09.0", :modif #inst "2007-02-19T23:00:00.000000000-00:00"}

;; eu.oca.jclojure=> (def records2 (readObject "KR  58"))
;; jclojure.clj : readObject : Reading Coordonnées...
;; #'eu.oca.jclojure/records2
;; eu.oca.jclojure=> records2
;; ()

(defn readObject
  "Read the table Coordonnées with Nom as input and display value of object in parameter."
  [name]
  
  (println "jclojure.clj : readObject : Reading Coordonnées...")

 
					;(jdbc/query db ["SELECT * FROM Sigles"])
    ;; WARNING: db est accessible meme en dehors du LET ! il semblerait que en clojure def definit des variables globales
    
    (jdbc/query db
                [(str "SELECT * FROM Coordonnées WHERE Nom='" name "'")])

 
  )

;; TODO:
;; debug verifier * entre int et float ???
;; 
