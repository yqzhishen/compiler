# Lab 7 实验报告

#### 姓名：杨迁		学号：19231120



Lab 7 的主要内容是实现数组，包括数组初始化、数组元素访问、数组元素赋值。

### 语法分析和类关系说明

虽然 lab 7 只要求实现一维和二维数组，但其实限制维数还不如不限制来得方便（而且还可以顺带做挑战实验）。所以数组的相关内容会大量使用递归方法。比如说数组初始化的语法分析就是递归的（伪代码，实际使用 java 实现）：

```python
def buildArrayInitializer(dimension):
    if dimension == 0:
        return buildExpression()
    read('{')
    while nextSym() != '}':
        self.initializers.add(buildArrayInitializer(dimension - 1))  # recursive call
    read("}")
    return self
```

语法分析的时候并不会检查数组维度的正确性以及初始化器的长度、深度，只分析一个形式上正确的初始化器。`IArrayInitializer`是数组初始化器的接口，`ArrayInitializer`类和`Expr`类都实现该接口，表达式`Expr`可以视为是一个 0 维的数组初始化器。

### 数组初始化

借助上面的数组初始化器，就可以把数组的初始化整合进`ConstDecl`和`VarDecl`了。首先调用各维度表达式的`calculate()`方法计算其实际值，如果不是可计算的常量表达式则会直接抛出异常；然后用计算结果替换各维度表达式。

数组形状（IR 中的数据类型）：格式为`"[<size> x "{<dim>} + "i32" + "]"{dim}`，其中`size`是各维长度，`dim`是维数，`{<num>}`代表字符串循环拼接`num`次。

获取数组头部：以二维数组`int a[2][3]`为例，获取其头部的方法就是：

```ir
%1 = getelementptr [2 x [3 x i32]], [2 x [3 x i32]]* %1, i32 0, i32 0, i32 0
```

随后调用`memset`方法将全部元素置为 0，然后开始根据初始化器逐一为元素赋值。这个赋值过程使用的是头部（head，直接使用前面`memset`获取的数组头部）+ 偏移（offset）的递归方式。大致思路如下：

```python
def initializeArray(size, offset, shape, initializer):
    instructions = []
    length = shape[0]  # 本维度的最大长度
    initializers = initializer.initializersOfThisDimension()  # 本维度的初始化器列表
    step = size / length  # 本维度的步长，等于总体积（前面已计算过）除以本维度长度
    for index from 0 to min(length, initializers.size):
        if shape.size == 1:  # 当前是一维数组，也即此时的初始化器列表中一定全部是表达式
            location = offset + index * step  # 这就是本次要初始化的元素距离数组头部的偏移值
            instructions.add("getelementptr i32, i32* %head, i32 %location")
        else:  # 如果当前不是一维数组，也就是说要初始化的是数组的一个子部分，递归调用方法
            instructions.addAll(initializeArray(
                step,  # 子数组体积 size 就是当前维度的步长（每步初始化多大的空间）
                offset + index * step,  # 子数组基础偏移 offset 就是当前的偏移加上步长乘以下标
                shape[1:],  # 子数组形状 shape 就是当前 shape 除去当前维度
                initializers[index]  # 子数组要使用的初始化器 initializer 就是当前维度初始化器列表中对应的初始化器
            ))
    return instructions
```

对于全局数组，大致思路不变，但因为全局数组不能调用`memset`赋 0，所以要先对初始化器进行广播，即在计算完数组形状后将省略的初始化器全部补成 0；如果一个子数组的初始化器被省略了，则补的是`zeroinitializer`。

### 数组元素赋值和访问

这两个操作相对来说容易很多。对于数组元素赋值，使用`getelementptr`获取元素地址，然后当做变量进行`store`操作即可。对于数组元素访问，则是在上一步的基础上将元素值`load`出来。
