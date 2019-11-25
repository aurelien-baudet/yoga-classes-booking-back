<#import "fragments/layout.txt.ftl" as layout>
<#import "fragments/utils.txt.ftl" as utils>
<#import "fragments/class.txt.ftl" as class>
<#import "fragments/information.txt.ftl" as information>
<@layout.header />
Changement de lieu

	<@utils.indent>
    Le cours du ${statics['fr.yoga.booking.util.DateRangeUtil'].format(data.scheduledClass.start, data.scheduledClass.end)} sera dispensé à ${data.newPlace.name}
	</@>
	

---------------------------------------------

<@class.classPreview scheduledClass=data.scheduledClass place=data.newPlace />
<@layout.footer />
