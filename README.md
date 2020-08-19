启动说明
-
- 此项目使用ElasticSearch7.5版本，需要启动对应版本的ES
- po类中使用lombok简化代码，需要下载idea对应版本的lombok插件

- 两个入口：
1. /parse/{keyword}传需要爬取的关键字，从京东上爬取（名称name，价格price，图片img）
2. /search/{keyword}/{pageNo}/{pageSize}，传入搜索关键字（需是在ES数据库中已爬取的），前端页面通过vue+axios进行数据传递显示

技术选型
- 
- FastJson：将解析页面的数据以json格式进行转换(PageContentService)
- Jsoup：页面爬取数据（HtmlParseUtil）
- ElasticSearch：爬取页面后的数据库，在此数据库中进行搜索(PageContentService)
- Vue、axios：前后端交互数据，在页面上显示(index.html)

