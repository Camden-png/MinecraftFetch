Push-Location code/SkriptUtils
mvn clean package
mvn package
Remove-Item ../../plugins/SkriptUtils-*.jar
Remove-Item target/skriptutils-*-shaded.jar
Copy-Item target/SkriptUtils-*.jar ../../plugins
Pop-Location
