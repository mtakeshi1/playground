import java.lang.invoke.{MethodHandle, MethodHandles, MethodType, VarHandle}

object AST {

  case class Context(vars: scala.collection.mutable.Map[String, Node] = scala.collection.mutable.Map(), parent: Option[Context] = None) {
    def get(name: String): Node = {
      vars.get(name).orElse(parent.map(_.get(name))).get
    }

    def set(name: String, v: Node): Unit = vars(name) = v
  }

  trait Node {
    def apply(ctx: Context): Any = eval(ctx)

    def eval(ctx: Context): Any

    def asBoolean(ctx: Context):Boolean = eval(ctx).asInstanceOf[Boolean]

    def asInt(ctx: Context):Int = eval(ctx).asInstanceOf[Int]

    def toMethodHandle: MethodHandle = ???
  }

  case class Loop(condition: Node, body: Node) extends Node {
    override def eval(ctx: Context): Any = {
      while (condition.asBoolean(ctx)) body.eval(ctx)
    }
  }

  case class If(condition: Node, ifTrue: Node, ifFalse: Node) extends Node {
    def this(c: Node, t: Node) = this(c, t, Nothing)

    override def eval(ctx: Context): Any = if (condition.asBoolean(ctx)) ifTrue(ctx) else ifFalse(ctx)
  }

  case object Nothing extends Node {
    override def eval(ctx: Context): Any = ()

    override val toMethodHandle: MethodHandle = MethodHandles.dropArguments(MethodHandles.constant(classOf[Any], ()), 0, classOf[Context])

  }

  case class Define(name: String, node: Node) extends Node {
    override def eval(ctx: Context): Any = {
      ctx.set(name, node)
    }
  }

  case class Get(name: String) extends Node {
    override def eval(ctx: Context): Any = ctx.get(name)

    override def toMethodHandle: MethodHandle = {
      val mh = MethodHandles.publicLookup().findVirtual(classOf[Context], "get", MethodType.methodType(classOf[Node], classOf[String]))
      val mh2 = MethodHandles.insertArguments(mh, 1, name);
      mh2
    }
  }

  case class Constant(a: Any) extends Node {
    override def eval(ctx: Context): Any = a

    override def toMethodHandle: MethodHandle = {
      MethodHandles.dropArguments(MethodHandles.constant(classOf[Any], a), 0, classOf[Context])
    }
  }

  object ListMethodHandles {
    val listType = Class.forName("scala.collection.immutable.List")
    val emptyListHandle = MethodHandles.constant(listType, List()) // () -> List
    val prepended = MethodHandles.publicLookup().findVirtual(listType, "prepended", MethodType.methodType(classOf[Any], classOf[Any]))
  }

  case class ExpList(nodes: Node*) extends Node {
    override def eval(ctx: Context): Any = (for(n <- nodes) yield n(ctx)).toList.last

    private def transformHandle(n: List[Node]): MethodHandle = {
      n match {
        case Nil => MethodHandles.dropArguments(ListMethodHandles.emptyListHandle, 0, classOf[Context])
        case head :: next => {

          ???
        }
      }
    }

    override def toMethodHandle: MethodHandle = transformHandle(nodes.toList)


  }

  def main(args: Array[String]): Unit = {

    val mh = Constant(1).toMethodHandle
    val ctx = Context()
    println(mh.invokeWithArguments(ctx))
  }

}
