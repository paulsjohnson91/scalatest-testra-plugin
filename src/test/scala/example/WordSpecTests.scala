import org.scalatest._
import collection.mutable.Stack
import org.scalatest.Tag

class WordSpecTests extends WordSpec with GivenWhenThen with Matchers with BeforeAndAfterAll {
  override def beforeAll(): Unit = {
    info("", Some(tags))
  }

    object MyLovelyTags extends Tag("MyLovelyTags")


  "Stack check feature" should {
    "stack should pop last" taggedAs (MyLovelyTags) in {
      Given("I create a stack with two ints")
      val stack = new Stack[Int]
      stack.push(1)
      stack.push(2)
      Then("The second value is popped first")
      stack.pop() should be(2)
      stack.pop() should be(1)
    }
  }

}
