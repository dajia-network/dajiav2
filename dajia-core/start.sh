application_properties=`find src -name application.properties -print`

sed 's/3306\/dajia\?/3306\/dajia_test\?/g' $application_properties > /tmp/sed.a
mv /tmp/sed.a $application_properties

scheduler_src_file=`find src -name ScheduledTasks.java`

sed '/@Scheduled/d' $scheduler_src_file  > /tmp/sed.b
mv /tmp/sed.b $scheduler_src_file

exit

echo ""
echo ""
echo ""
echo "###################################################################"
echo "   _______       ___            __   __       ___      "
echo "  |       \     /   \          |  | |  |     /   \     " 
echo "  |  .--.  |   /  ^  \         |  | |  |    /  ^  \    " 
echo "  |  |  |  |  /  /_\  \  .--.  |  | |  |   /  /_\  \   " 
echo "  |  '--'  | /  _____  \ |  '--'  | |  |  /  _____  \  " 
echo "  |_______/ /__/     \__\ \______/  |__| /__/     \__\ " 
echo "								         "
echo "###################################################################"
echo ""
echo ""
                                                     

 mvn clean spring-boot:run -Drun.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"

