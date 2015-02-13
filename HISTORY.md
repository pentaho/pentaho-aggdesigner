# Aggregate Designer release history and change log

For a full list of releases, see <a href="https://github.com/julianhyde/pentaho-aggatedesigner/releases">github</a>.

## <a href="https://github.com/julianhyde/pentaho-aggdesigner/releases/tag/pentaho-aggdesigner-5.1.5-jhyde">5.1.5-jhyde</a> / 2015-01-27

* Make commons-logging dependency "compile"

## <a href="https://github.com/julianhyde/pentaho-aggdesigner/releases/tag/pentaho-aggdesigner-5.1.4-jhyde">5.1.4-jhyde</a> / 2015-01-26

* Disable some tests; all Travis now runs clean
* Use hsqldb, rather than MySQL, as default test database; upgrade hsqldb
* Enable Travis CI
* Add history, LICENSE, NOTICE
* Fix <a href="https://github.com/julianhyde/pentaho-aggdesigner/issues/21">[AGGDES-1]</a>,
  "Make commons-logging and dom4j dependencies "provided"
* Test the algorithm on a more realistic schema, with state depending on
  zipcode

## <a href="https://github.com/julianhyde/pentaho-aggdesigner/releases/tag/pentaho-aggdesigner-5.1.3-jhyde">5.1.3-jhyde</a> / 2014-09-30

* Specify git version in `maven-release-plugin`, otherwise can't
  commit during release process
* Change scm to allow pushes during release process
* Remove version from `build.properties`: it confuses maven release
  plugin
* Resolve SNAPSHOT dependencies
* Change distribution repo just while we make a release
* Add parent POM
* Move pure algorithm classes into a new maven module
  `pentaho-aggdesigner-algorithm`, which does not depend on mondrian
* Test MonteCarlo algorithm on 10 attributes
* Code hygiene: Remove `@author`, `@version`, `@since` tags; remove
  trailing spaces; expand tabs
* Use an `ArrayDeque` rather than a `LinkedList`; better memory usage
* Widen return type of `Schema.getAttributes()`
* Add test resources to `subfloor.xml`; fixes test suite
* Convert to maven
* Fork from
  <a href="https://github.com/pentaho/pentaho-aggdesigner">Pentaho</a>
* Change license from GPL version 2 to Apache Software License 2.0

For ancient history, see
<a href="https://github.com/pentaho/pentaho-aggdesigner">Pentaho's github</a>.
