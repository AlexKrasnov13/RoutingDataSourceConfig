# RoutingDataSourceConfig

This is a data source for use separately for reading from a replica and writing to a workshop.
Configuration example in application.yml.
To read from the replica, you must specify the annotations over the method in the service @ReadOnlyConnection
and @Transactional (readOnly = true).
