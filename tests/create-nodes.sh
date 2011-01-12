mkdir -p demo && cd demo
mkdir -p 1 && cd 1 && java -jar ~/Work/Research/Code/eXO/eXO.jar -u 'N. Ntarmos' -r 'On the road' -w 8081 -d 5009 -b localhost:5009 > log 2>&1 & cd ..
mkdir -p 2 && cd 2 && java -jar ~/Work/Research/Code/eXO/eXO.jar -u 'A. Loupasakis' -r 'Home' -w 8082 -d 5010 -b localhost:5009 > log 2>&1 & cd ..
mkdir -p 3 && cd 3 && java -jar ~/Work/Research/Code/eXO/eXO.jar -u 'P. Triantafillou' -r 'Asilomar' -w 8083 -d 5011 -b localhost:5009 > log 2>&1 & cd ..
mkdir -p 4 && cd 4 && java -jar ~/Work/Research/Code/eXO/eXO.jar -u 'J. Doe' -r 'Somewhere' -w 8084 -d 5012 -b localhost:5009 > log 2>&1 & cd .. 
mkdir -p 5 && cd 5 && java -jar ~/Work/Research/Code/eXO/eXO.jar -u 'H. Wotsit' -r 'Someplace' -w 8085 -d 5013 -b localhost:5009 > log 2>&1 & cd .. 
