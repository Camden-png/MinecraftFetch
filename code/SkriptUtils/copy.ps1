mvn clean package
mvn package
rm ../../plugins/SkriptUtils-*.jar
rm target/skriptutils-*-shaded.jar
cp target/SkriptUtils-*.jar ../../plugins
