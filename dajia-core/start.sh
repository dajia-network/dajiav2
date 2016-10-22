#############################
# 修改数据库为dajia_test
#############################

application_properties=`find src -name application.properties -print`
sed 's/3306\/dajia\?/3306\/dajia_test\?/g' $application_properties > /tmp/sed.a
mv /tmp/sed.a $application_properties

#############################
# 删除定时任务的事件 
#############################

scheduler_src_file=`find src -name ScheduledTasks.java`
sed '/@Scheduled/d' $scheduler_src_file  > /tmp/sed.b
mv /tmp/sed.b $scheduler_src_file


exit


echo ""
echo ""
echo ""
echo "###################################################################"
cat  scripts/avatar.txt
echo ""
echo "###################################################################"
echo ""
echo ""
                                                     
mvn -X clean spring-boot:run -Drun.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"

