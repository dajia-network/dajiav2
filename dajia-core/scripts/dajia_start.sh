
## NGXIN_HOME
nginx_home=/home/dajia/env/nginx
nginx_pid=/home/dajia/log/nginx.pid

## JarHome
source_folder=/home/dajia/dajiav2/dajia-core

## webapp
main_folder=$source_folder/src/main
webapp_folder=$main_folder/webapp

## stop nginx
if [[ -e $nginx_pid ]]; then 
	$nginx_home/sbin/nginx -s stop
	echo "nginx down"
	## force clear pid file
	if [[ -e $nginx_pid ]]; then 
		rm -f $nginx_pid
		echo "pid removed"	
	fi
fi

cd $main_folder
git reset --hard
git checkout .
git pull

cd $source_folder && mvn clean compile package -Dmaven.test.skip=true
echo "packed"

cd ~/bin

## copy static files
cp -rf $webapp_folder/* $nginx_home/html
rm -rf $webapp_folder
echo "static files moved"

$nginx_home/sbin/nginx && echo "nginx up"

# kill springboot
ps aux | grep java | grep 'dajia-core' | awk '{print $2}' | xargs kill -9 
echo "java down" 

cd $source_folder
nohup java -jar target/dajia-core-0.0.1-SNAPSHOT.jar >/home/dajia/log/dajia.log &

#str=$"/n"
#sstr=$(echo -e $str)  
#echo $sstr  
echo "java up"

tail -f /home/dajia/log/dajia.log

