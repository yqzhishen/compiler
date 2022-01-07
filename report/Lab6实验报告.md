# Lab 6 实验报告

#### 姓名：杨迁		学号：19231120



Lab 6 的主要内容是实现`while`循环。由于逻辑表达式的部分已经在 lab 4 做过了，所以可以直接沿用。循环的大致基本块结构是这样的：

```ir
head:
    ; instructions of the loop condition
    br i1 %result, label %exec, label %pass
exec:
    ; instructions within the loop block
    br label %pass
pass:
    ; other instructions after the loop
```

明确了这一点就可以实现不带`continue`和`break`的循环了。而由于我是依然使用数字标号的，所以需要将 label 也纳入标号顺序中，所以这里的 label 都是先`new`出来以后填入指令列表，轮到它编号时再回填编号的。

接下来就是要实现`break`和`continue`。这就要求当循环体内部的语句在生成中间代码时要能够访问到当前这层循环的头和尾在什么地方（也就是上面代码块里的`head`和`pass`标签），相当于是一个可全局访问的内容。所以这里实现了一个`LoopScope`类：

```java
public class LoopScope {

    private record Scope(Label head, Label tail) { }

    private static final LoopScope scope = new LoopScope();

    public static LoopScope getInstance() {
        return scope;
    }

    private LoopScope() { }

    private final Stack<Scope> scopes = new Stack<>();

    public void pushLayer(Label head, Label tail) {
        scopes.push(new Scope(head, tail));
    }

    public void popLayer() {
        this.scopes.pop();
    }

    public Label head() {
        if (scopes.isEmpty())
            return null;
        return scopes.peek().head;
    }

    public Label tail() {
        if (scopes.isEmpty())
            return null;
        return scopes.peek().tail;
    }

}
```

大致实现思路就是采用一种栈式的结构，每一层循环在生成它的循环体的中间代码之前将`head`和`pass`标签压栈，里面的语句只要访问栈顶元素，就代表当前循环的头和尾。`break`就是跳到`pass`，`continue`就是跳到`head`。这里其实已经顺带完成了语义的约束，当访问栈时栈为空，就相当于在循环外部遇到了`break`和`continue`语句，这时候需要报错（虽然实验好像没有要求，但顺便做了）。当然这样并不能解决`break`和`continue`后还有语句的情况：

```c
while (1) {
    break;
    putint(1);
}
```

这种情况生成出来的中间代码是无法运行的，因为会违反基本块的定义（一个基本块最多只能有最后一条跳转语句）。但测试样例没有对这种情况加以要求，所以就没有特别判断。`if`语句中的`break`和`continue`还是可以正常处理的，因为`if`语句自带基本块的划分，比如这样：

```c
while (1) {
    if (1)
        break;
}
```

```ir
define dso_local i32 @main() {
    br label %1
    
1:
    %2 = icmp ne i32 1, 0
    br i1 %2, label %3, label %7
    
3:
    %4 = icmp ne i32 1, 0
    br i1 %4, label %5, label %6
    
5:
    br label %7
    
6:
    br label %1
    
7:
    ret i32 0
}
```



