# version: 2016/09/26 把nohup的日志删掉 改成用软链接把当天的日志链接到原来的位置

## NGXIN_HOME
nginx_home=/home/dajia/env/nginx
nginx_pid=/home/dajia/log/nginx.pid

## JarHome
source_folder=/home/dajia/dajiav2/dajia-core
logs_folder=$source_folder/logs/

## webapp
main_folder=$source_folder/src/main
webapp_folder=$main_folder/webapp

## stop nginx
if [[ -e $nginx_pid ]]; then
	sudo $nginx_home/sbin/nginx -s stop
	echo "nginx down"
	## force clear pid file
	if [[ -e $nginx_pid ]]; then
		sudo rm -f $nginx_pid
		echo "pid removed"
	fi
fi

cd $main_folder
git reset --hard
# git checkout .
git pull

cd $source_folder && mvn clean compile package -Dmaven.test.skip=true
echo "packed"

cd ~/bin

## copy static files
cp -rf $webapp_folder/* $nginx_home/html
# rm -rf $webapp_folder
echo "static files moved"

sudo $nginx_home/sbin/nginx && echo "nginx up"

# kill springboot
ps aux | grep java | grep 'dajia-core' | awk '{print $2}' | xargs kill -9
echo "java down"

cd $source_folder
nohup java -jar target/dajia-core-0.0.1-SNAPSHOT.jar &

# logs shortcut
ln -sf $logs_folder/dajia.log.`date +%Y-%m-%d`.log dajia.log
ln -sf $logs_folder/dajia-access.`date +%Y-%m-%d`.log dajia-access.log
ln -sf $logs_folder/dajia-admin.log.`date +%Y-%m-%d`.log dajia-admin.log

#str=$"/n"
#sstr=$(echo -e $str)
#echo $sstr
echo "java up"

tail -f /home/dajia/log/dajia.log