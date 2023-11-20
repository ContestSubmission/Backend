## Grafana
The grafana instance is automatically provisioned. It can be reached under [localhost:3000](http://localhost:3000).

Authentication is disabled, no login is required, and you are automatically an admin.

Keep in mind that this is a development instance, and should not be used in production.<br>
There, either use a properly set up Grafana instance or a industry-grade solution like [Dynatrace](https://www.dynatrace.com/).


### Provisioned Resources

#### Data Sources
- [Prometheus](../metrics/docker-compose.yml)
- [Tempo](../tracing/docker-compose.yml)

#### Dashboards
- [JVM](http://localhost:3000/d/a1a9c881-fb28-491f-8837-d02846f33dde/jvm)
