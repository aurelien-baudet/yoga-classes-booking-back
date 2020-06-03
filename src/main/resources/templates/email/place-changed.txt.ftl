<#import "fragments/layout.txt.ftl" as layout>
<#import "fragments/utils.txt.ftl" as utils>
<#import "fragments/class.txt.ftl" as class>
<#import "fragments/information.txt.ftl" as information>
<@layout.header />
Changement de lieu

	<@utils.indent>
    Le cours du ${statics['fr.yoga.booking.util.DateRangeUtil'].format(scheduledClass.start, scheduledClass.end)} sera dispensé à ${newPlace.name}
	</@>
	

---------------------------------------------

<@class.classPreview scheduledClass=scheduledClass place=newPlace />
<@layout.footer />
