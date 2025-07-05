# 模板引擎

|             | Velocity                             | FreeMarker                                                | Mustache                              | Nunjucks                                                                   | SquirrellyJS                                                                  |
|:------------|:-------------------------------------|:----------------------------------------------------------|:--------------------------------------|:---------------------------------------------------------------------------|:------------------------------------------------------------------------------|
| 单行注释        | ## comment                           | <#-- -->                                                  | {{! comment }}                        | {# comment #}                                                              | {{! /* comment */ }}                                                          |
| 多行注释        | #\*<br>comment<br>\*#                |                                                           |                                       |                                                                            |                                                                               |
| 取值          | $var<br>$!var                        | ${var}                                                    | {{var}}                               | {{var}}                                                                    | {{var}}                                                                       |
| 取object值    | $object.prop<br>$object\["prop"\]    | ${object.prop}<br>{object.\["prop"\]}                     | {{object.prop}}                       | {{object.prop}}<br>{{object.\["prop"\]}}                                   | {{object.prop}}<br>{{object.\['prop'\]}}                                      |
| 取array值     | $array\[index\]                      | $array\[index\]                                           | {{array\[index\]}}                    | $array\[index\]                                                            | {{array.\[index\]}}                                                           |
| 模板内定义变量     | #set($var="literal")                 | <#assign var="literal" key2=[]>                           |                                       | {% set var, key2="literal" %}                                              |                                                                               |
| 条件指令        | #if($test)<br>#else<br>#end          | <#if test><#elseif test><#else></#if>                     | {{#test}}{{/test}}                    | {% if test %} {% elif test %} {% else %} {% endif %}                       | {{@if(test)}} {{#else}} {{/if}}                                               |
| 循环指令        | #foreach($item in $array)<br>#end    | <#list array as item><br><#else></#list>                  | {{#array}}{{.}}{{/array}}             | {% for item in array %} {% else %} {% endfor %}                            | {{@each(array)=>val,idx}} {{/each}}<br>{{@foreach(array)=>key,val{{/foreach}} |
| 跳出循环指令      | #break                               | <#break>                                                  |                                       |                                                                            |                                                                               |
| try-catch指令 |                                      | <#attempt><br>try<br><#recovery><br>catch<br></#attempt>  |                                       |                                                                            | {{@try}}<br>{{#catch=>error}<br>{{/try}}                                      |
| 调用          | $instance.func(args)                 | args?built-in-func<br>func(args)                          | {{#func}}{{/func}}                    | *{% filter func %} {% endfilter %}*                                        | {{! instance.func(args) }}                                                    |
| 静态包含        | #include("file.ext")                 | <#include "fragment.ftl" parse=false ignore_missing=true> |                                       | {% include "fragment.html" ignore missing %}                               |                                                                               |
| 动态包含        | #parse("fragment.vm")                | <#import "fragment.ftl" as f>                             | {{> fragment}}                        | {% import "fragment.html" as f %}                                          | {{@includeFile("fragment", "literal")/}}                                      |
| 定义动态模板      | #define($string)literal $var#end     | <#assign string>literal $var</#assign>                    | fragment.mustache:<br>literal {{var}} | {% set string %}literal $var{% endset %}                                   |                                                                               |
| 解析动态模板      | #evaluate($string)                   | expression?eval<br>string?interpre                        |                                       |                                                                            | {{@include("string", "literal")}}                                             |
| 中止解析        | #stop('$optMsg')                     | <#stop optMsg>                                            |                                       |                                                                            |                                                                               |
| 自定义指令       | #macro(ud $optArgs)<br>@d<br>#end    | <#macro ud optArgs><br><@ud "literal" />                  |                                       | {% macro ud(optArgs="optDefaultValue" %}<br>{% call d %}<br>{% endmacro %} | {{@ud(parameters)=>\[optArgs\]}}{{@d(args)}}{{/d}}{{/ud}}                     |
| 跳出解析范围      | #\[\[<br>#unparsed $content<br>\]\]# | <#noparse><br><#unparsed> $content<br></#noparse>         | {{{html}}}                            | {% raw {{ %}                                                               |                                                                               |
| 语言          | Java                                 | Java                                                      | C, Go, Java, JavaScript, PHP, Python  | JavaScript                                                                 | JavaScript                                                                    |
| 大小          | 518KB                                | 1.7MB                                                     | js: 20KB<br>Java: 83KB                | 20KB                                                                       | 10KB                                                                          |

Velocity标准取值和使用指令方式的前缀为"${"，后缀为"}"，如取值：${foo}；为避免NPE，可以使用空值，如:$!foo；也支持提供默认值或替代值，如：${foo | "bar"}, ${foo | $bar}

Velocity标准使用指令方式使用指令方式前缀为"#{"，后缀为"}"，如指令：#{if}($var)true#{else}false#{end}。

FreeMarker可以通过设置将语法由"<>"替换为"[]"，如：[=var]、[#if test][#end]。
FreeMarker支持默认值，如：${foo!"bar"}。
FreeMarker的选择指令不仅有if else，还支持switch case。
FreeMarker支持自定义函数，并在模板中引用。
FreeMarker对循环有很好支持，如判断下一个是否存在（item?has_next）、当前位置（item?index）、总数（item?counter）。

FreeMarker的更新频率比Velocity频繁。

FreeMarker、Nunjucks、Squirrelly支持将对象同时取key，value进行遍历，都支持对空字符串进行控制。

Mustache支持在js代码"Mustache.tags = ['<#', '>']"替换默认的分隔符"{{}}"，或者在模板中临时切换"{{=<% %>=}}"并换回"<%={{ }}=%>"。
Mustache的指令对应数据中的一个函数，与Java的模板引擎比较，更像是仅支持表达式，而缺乏内建标签，风格怪异，但功能依然强大。

Nunjucks由Mozilla维护的一个仿照Python模板引擎Jinja2的JavaScript实现。
Nunjucks支持像OOP中定义方法一样定义代码块(block)，使用继承(extends)来简化模板。
Nunjucks支持异步遍历对象：{% asyncEach item in items %} {% endeach %}、{% asyncAll item in items %} {% endall %}。
调用Nunjucks的内建函数有简便形式，如：{{ -3 | abs}}。

SquirrellyJS参考了Mustache、Handlebars、EJS、Nunjucks、Swig、doT.js，内建函数简便形式同Nunjucks。

Handlebars语法兼容Mustache，内建了if、each等指令（Handlebars称之为"Helper"），更加形式化。Handlebars也有多种语言，js版本大小约80KB，Java版本大小约931KB。

EJS是一个js模板引擎，语法类似JSP，使用"<% %>"来标记值、指令位置，如：<%=var %>、<% if test { %><% } %>。
EJS允许修改当前或全局的分隔符"%"为指定分隔符。

htmx通过自定义属性来发起ajax，结合mustache、handlebars、nunjucks或xslt这4种客户端模板之一，将template标签内的开发者用特定语言写的模板进行解析，结合返回的XML/JSON数据渲染到指定DOM位置。

