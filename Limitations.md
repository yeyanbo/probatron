# XPath 1.0 only #

Probatron currently uses an XPath 1.0 engine to evaluate XML documents, therefore schema XPaths must conform to XPath 1.0 syntax.

If your schema root element specifies the `queryBinding` attribute, this **must** have the (case-insensitive) value "`xpath`".