# jgroups-playground
Basing on Piotr Nowicki project on JGroups within JBoss AS7
You can see Piotr's original work here:
http://piotrnowicki.com/2013/02/using-jgroups-directly-from-jboss-as-7-component/

It's just simple lib with service activator being made abstract class
so it can't be used for creating multiple service activators for different channels,
and web app which uses it to synch across multiple JBoss AS 7 nodes.
