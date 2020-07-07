import org.scalatest.featurespec.AnyFeatureSpec
import com.gu.spy._

class ReaderTest extends AnyFeatureSpec {

  Feature("Read eclipse project data")
  {

    Scenario("A regular Mendix 8.10 project deployed for eclipse")
    {
      val classLoader = Thread.currentThread.getContextClassLoader
      val projectData = Reader.read(
        classpathStream = classLoader.getResourceAsStream("sample/eclipse/.classpath"),
        projectStream = classLoader.getResourceAsStream("sample/eclipse/.project"),
        launchStream = classLoader.getResourceAsStream("sample/eclipse/practice1.launch"),
      )
      val expected = """ProjectData
                       |  projectName = practice1
                       |  environmentVariables: Iterable
                       |    Tuple2
                       |      _1 = MX_INSTALL_PATH
                       |      _2 = C:/Program Files/Mendix/8.7.0.1476
                       |    Tuple2
                       |      _1 = M2EE_ADMIN_PORT
                       |      _2 = 8090
                       |    Tuple2
                       |      _1 = MXCONSOLE_SERVER_URL
                       |      _2 = http://127.0.0.1:8090/
                       |    Tuple2
                       |      _1 = M2EE_CONSOLE_PATH
                       |      _2 = C:/Program Files/Mendix/8.7.0.1476/modeler/MendixConsoleLog.exe
                       |    Tuple2
                       |      _1 = MXCONSOLE_BASE_PATH
                       |      _2 = C:/Users/nima-windows/Documents/Mendix/practice1/deployment
                       |    Tuple2
                       |      _1 = MXCONSOLE_RUNTIME_PATH
                       |      _2 = C:/Program Files/Mendix/8.7.0.1476/runtime
                       |    Tuple2
                       |      _1 = MXCONSOLE_RUNTIME_LISTEN_ADDRESSES
                       |      _2 = *
                       |    Tuple2
                       |      _1 = M2EE_ADMIN_PASS
                       |      _2 = 1
                       |    Tuple2
                       |      _1 = MXCONSOLE_RUNTIME_PORT
                       |      _2 = 8080
                       |  classpath: Iterable
                       |    C:/Program Files/Mendix/8.7.0.1476/runtime/launcher/runtimelauncher.jar
                       |    C:/Program Files/Mendix/8.7.0.1476/runtime/bundles/com.mendix.json.jar
                       |    C:/Program Files/Mendix/8.7.0.1476/runtime/bundles/com.mendix.logging-api.jar
                       |    C:/Program Files/Mendix/8.7.0.1476/runtime/bundles/com.mendix.m2ee-api.jar
                       |    C:/Program Files/Mendix/8.7.0.1476/runtime/bundles/com.mendix.public-api.jar
                       |    C:/Program Files/Mendix/8.7.0.1476/runtime/bundles/javax.servlet-api.servlet.jar
                       |    userlib/com.ibm.db2.jcc.db2jcc-db2jcc4.jar
                       |    userlib/com.microsoft.sqlserver.mssql-jdbc-7.2.1.jre8.jar
                       |    userlib/com.oracle.database.ha.ons-12.2.0.1.jar
                       |    userlib/com.oracle.database.ha.simplefan-12.2.0.1.jar
                       |    userlib/com.oracle.database.jdbc.ojdbc8-12.2.0.1.jar
                       |    userlib/com.oracle.database.jdbc.ucp-12.2.0.1.jar
                       |    userlib/com.oracle.database.security.oraclepki-12.2.0.1.jar
                       |    userlib/com.oracle.database.security.osdt_cert-12.2.0.1.jar
                       |    userlib/com.oracle.database.security.osdt_core-12.2.0.1.jar
                       |    userlib/com.sap.cloud.db.jdbc.ngdbc-2.3.58.jar
                       |    userlib/com.zaxxer.HikariCP-2.6.1.jar
                       |    userlib/org.hsqldb.hsqldb-2.4.1.jar
                       |    userlib/org.mariadb.jdbc.mariadb-java-client-2.4.0.jar
                       |    userlib/org.postgresql.postgresql-42.2.9.jar
                       |    userlib/org.slf4j.slf4j-api-1.7.21.jar
                       |  mainClass = com.mendix.container.boot.Main
                       |  vmArguments: Iterable
                       |    -Djava.net.preferIPv4Stack=true
                       |    -DMX_LOG_LEVEL=INFO
                       |    -Djava.library.path=C:/Program Files/Mendix/8.7.0.1476/runtime/lib/x64;C:/Users/nima-windows/Documents/Mendix/practice1/deployment/model/lib/userlib
                       |    -Dfile.encoding=UTF-8
                       |    -Djava.io.tmpdir=C:/Users/nima-windows/Documents/Mendix/practice1/deployment/data/tmp
                       |    -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
                       |  programArguments: Iterable
                       |    C:/Users/nima-windows/Documents/Mendix/practice1/deployment
                       |  workingDirectory = C:/Users/nima-windows/Documents/Mendix/practice1/deployment
                       |""".stripMargin
      assert(expected == projectData.spy)
    }

  }

}
