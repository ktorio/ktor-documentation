<#import "_layout.ftl" as layout />
<@layout.header>
    <div>
        <h3>Create article</h3>
        <form action="/articles" method="post">
            <p>
                <input type="text" name="title">
            </p>
            <p>
                <textarea name="body"></textarea>
            </p>
            <p>
                <input type="submit">
            </p>
        </form>
    </div>
</@layout.header>
