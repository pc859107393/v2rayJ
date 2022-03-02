# PAC说明

## 什么是Pac

[点击访问](https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Proxy_servers_and_tunneling/Proxy_Auto-Configuration_PAC_file)

PAC是一个js脚本，核心是利用js函数实现请求代理转发。核心函数为：

```javascript
function FindProxyForURL(url, host) {
  // ...
}
```

依赖规则文件进行资源控制，控制资源的核心js代码如下：

```javascript
Filter.knownFilters = createDict();
Filter.elemhideRegExp = /^([^\/\*\|\@"!]*?)#(\@)?(?:([\w\-]+|\*)((?:\([\w\-]+(?:[$^*]?=[^\(\)"]*)?\))*)|#([^{}]+))$/;
Filter.regexpRegExp = /^(@@)?\/.*\/(?:\$~?[\w\-]+(?:=[^,\s]+)?(?:,~?[\w\-]+(?:=[^,\s]+)?)*)?$/;
Filter.optionsRegExp = /\$(~?[\w\-]+(?:=[^,\s]+)?(?:,~?[\w\-]+(?:=[^,\s]+)?)*)$/;
```

## 规则文件
我们常用的规则文件俗名为：gfwlist.txt，是经过base64编码后的地址集合，主要核心内容大概如下：

```javascript
@@|http://translate.google.cn
@@|http://www.google.cn/maps
.zoozle.net
||zophar.net
||zaobao.com.sg
```

## 自定义规则
支持通过 --user-rule 自定义单个规则或 --user-rule-from 读入自定义规则文件，这两个参数均可重复使用。

自定义规则文件可参考sample/user-rules.txt

自定义规则的语法与gfwlist相同，使用AdBlock Plus过滤规则( http://adblockplus.org/en/filters )，简述如下:

- 通配符支持，如 *.example.com/* 实际书写时可省略 * 为 .example.com/
- 正则表达式支持，以 \ 开始和结束，如 \[\w]+:\/\/example.com\\
- 例外规则 @@ ，如 @@*.example.com/* 满足 @@ 后规则的地址不使用代理
- 匹配地址开始和结尾 | ，如 |http://example.com 、 example.com| 分别表示以 http://example.com 开始和以 example.com 结束的地址
- || 标记，如 ||example.com 则 http://example.com https://example.com ftp://example.com 等地址均满足条件
- 注释 ! 如 ! Comment

- 配置自定义规则时需谨慎，尽量避免与gfwlist产生冲突，或将一些本不需要代理的网址添加到代理列表

规则优先级从高到底为: user-rule > user-rule-from > gfwlist