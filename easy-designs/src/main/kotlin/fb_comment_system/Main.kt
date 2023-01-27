package fb_comment_system

import java.lang.Error

/**
 * One of the good thing about this implementation is that we have user doing everything
 * creating post and deleting and editing it also
 */

class Comment(private var body: String) {
    private var commentId = ""
    private var parentComment: Comment? = null
    private val nestedComments = mutableListOf<Comment>()

    companion object {
        private var uniqueCompanionCounter = 0
        fun getUniqueId() = uniqueCompanionCounter++
    }

    init {
        commentId = "comment: ${getUniqueId()}"
    }

    fun getParent() = parentComment

    fun getCommentId() = commentId

    fun setParentComment(comment: Comment) {
        if (commentId == comment.getCommentId())
            throw Error("one cannot be parent of itself")
        parentComment = comment
    }

    fun addNestedComment(nestedComment: Comment) {
        nestedComments.add(nestedComment)
    }

    fun deleteNestedComment(commentId: String): Boolean {
        return nestedComments.removeIf {
            it.commentId == commentId
        }
    }

    fun printCommentAndItsChildren() {
        println("*************** Comment $commentId ***************")
        println("Comment Body: $body")
        nestedComments.forEach {
            print("\t nested ${it.commentId} -> ${it.body}\n")
        }
        println("**************************************************\n")
    }

    fun changeBodyTo(editedComment: String) {
        body = editedComment
    }

    fun getNestedComments() = nestedComments

}

class Post constructor(
    val description: String,
) {
    private var postId = ""

    private var comments = mutableListOf<Comment>()

    companion object {
        private var uniqueCompanionCounter = 0
        fun getUniqueId() = uniqueCompanionCounter++
    }

    init {
        postId = "post: ${getUniqueId()}"
    }

    fun getPostId() = postId

    fun getComments() = comments

    fun addComment(comment: Comment) {
        comments.add(comment)
    }

    fun deleteComment(commentId: String): Boolean {
        val isDeleted = comments.removeIf {
            it.getCommentId() == commentId
        }
        if(isDeleted)
            return true
        // see nested comments
        comments.forEach { cmt->
            if(cmt.deleteNestedComment(commentId)){
                return true
            }
        }
        return false
    }

    fun printPostAlongWithComments() {
        println("Post: $description")
        comments.forEach {
            it.printCommentAndItsChildren()
        }
        println()
    }

}

class User constructor(val name: String) {

    private var userId = ""

    private var posts = mutableListOf<Post>()

    companion object {
        private var uniqueCompanionCounter = 0
        fun getUniqueId() = uniqueCompanionCounter++
    }

    init {
        userId = "user: ${getUniqueId()}"
    }

    fun createPost(
        post: Post
    ) {
        posts.add(post)
    }

    fun getAllPosts() = posts

    fun commentToPost(postId: String, comment: Comment) {
        val post = posts.find {
            it.getPostId() == postId
        }
        post?.addComment(comment)
    }

    fun replyToComment(comment: Comment, nestedComment: Comment) {
        // in DB we will not maintain hierarchy
        nestedComment.setParentComment(comment)

        var topLevelComment: Comment = nestedComment.getParent()!!
        while (topLevelComment.getParent() != null) {
            topLevelComment = topLevelComment.getParent()!!
        }
        topLevelComment.addNestedComment(nestedComment)
    }

    fun editComment(post: Post, commentId: String, editedComment: String) {
        val comments = post.getComments()
        comments.forEach {comment->
            if(comment.getCommentId() == commentId){
                comment.changeBodyTo(editedComment)
                return
            }
            comment.getNestedComments().forEach { nComment->
                if(nComment.getCommentId() == commentId){
                    nComment.changeBodyTo(editedComment)
                    return
                }
            }
        }
    }

    fun deleteCommentFromPost(post: Post, commentId: String){
        post.deleteComment(commentId)
    }
}

fun main() {
    val saurabh = User("Saurabh")
    val post = Post("Sunset in Jaipur")
    val commentOne = Comment("First Comment in the name of Saurabh")
    val commentOneReply = Comment("Aur bhai kya baat hain")
    val commentOne2ndNestedReply = Comment("Yo Jaipur rocks ....")

    val commentTwo = Comment("Second Comment in the name of Nikhil")
    val commentTwoReply = Comment("Haan haan Mere Saath hi gaya hain")

    val commentThree = Comment("Third Comment in the name of Nitin")
    val commentThreeReply = Comment("Mujhe kyu nahi bulaya")

    saurabh.createPost(post)

    saurabh.commentToPost(
        post.getPostId(),
        commentOne
    )
    saurabh.replyToComment(
        comment = commentOne,
        nestedComment = commentOneReply,
    )
    saurabh.replyToComment(
        comment = commentOneReply,
        nestedComment = commentOne2ndNestedReply,
    )

    saurabh.commentToPost(
        post.getPostId(),
        commentTwo
    )
    saurabh.replyToComment(
        comment = commentTwo,
        nestedComment = commentTwoReply,
    )

    saurabh.commentToPost(
        post.getPostId(),
        commentThree
    )
    saurabh.replyToComment(
        comment = commentThree,
        nestedComment = commentThreeReply,
    )

    val allPosts = saurabh.getAllPosts()
    allPosts.forEach {
        it.printPostAlongWithComments()
    }

    // edit comment with comment id 6, Mujhe kyu nahi bulaya -> mujhe bhi bula lete
    saurabh.editComment(post, commentThreeReply.getCommentId(), "mujhe bhi bula lete")
    println("after editing comment in post we have: ")
    allPosts.forEach {
        it.printPostAlongWithComments()
    }

    saurabh.editComment(post, commentThree.getCommentId(), "On behalf of Nitin Boss")
    println("after editing comment in post we have: ")
    allPosts.forEach {
        it.printPostAlongWithComments()
    }

    println("deleting comment id : 2 and then comment: 0")
    println("deleted comment 2")
    saurabh.deleteCommentFromPost(
        post = post,
        commentId = commentOne2ndNestedReply.getCommentId(),
    )
    allPosts.forEach {
        it.printPostAlongWithComments()
    }
    println("deleted comment 0")
    saurabh.deleteCommentFromPost(
        post = post,
        commentId = commentOne.getCommentId(),
    )
    allPosts.forEach {
        it.printPostAlongWithComments()
    }
}