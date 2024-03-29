package com.example.vknewsclient.data.mapper

import com.example.vknewsclient.data.model.fave.FaveResponseDto
import com.example.vknewsclient.data.model.general.CommentsResponseDto
import com.example.vknewsclient.data.model.recomendation.NewsFeedResponseDto
import com.example.vknewsclient.data.model.user.UserResponseDto
import com.example.vknewsclient.domain.entity.FeedPost
import com.example.vknewsclient.domain.entity.PostComment
import com.example.vknewsclient.domain.entity.StatisticItem
import com.example.vknewsclient.domain.entity.StatisticType
import com.example.vknewsclient.domain.entity.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.absoluteValue

class NewsFeedMapper @Inject constructor() {

    fun mapResponseToPosts(responseDto: NewsFeedResponseDto): List<FeedPost> {
        val result = mutableListOf<FeedPost>()

        val posts = responseDto.newsFeedContent.posts
        val groups = responseDto.newsFeedContent.groups

        for (post in posts) {
            val group = groups.find { it.id == post.communityId.absoluteValue } ?: break
            val feedPost = FeedPost(
                id = post.id,
                communityId = post.communityId,
                communityName = group.name,
                publicationDate = mapTimestampToDate(post.date),
                communityImageUrl = group.imageUrl,
                contentText = post.text,
                contentImageUrl = post.attachments?.firstOrNull()?.photo?.photoUrls?.lastOrNull()?.url,
                statistics = listOf(
                    StatisticItem(type = StatisticType.LIKES, post.likes.count),
                    StatisticItem(type = StatisticType.VIEWS, post.views.count),
                    StatisticItem(type = StatisticType.SHARES, post.reposts.count),
                    StatisticItem(type = StatisticType.COMMENTS, post.comments.count)
                ),
                isLiked = post.likes.userLikes > 0
            )
            result.add(feedPost)
        }
        return result
    }

    fun commentsResponseToPostComments(response: CommentsResponseDto): List<PostComment> {
        val result = mutableListOf<PostComment>()
        val comments = response.content.comments
        val profiles = response.content.profiles

        for (comment in comments) {
            if (comment.text.isBlank()) continue
            val author = profiles.firstOrNull { it.id == comment.fromId } ?: continue
            val postComment = PostComment(
                id = comment.id,
                authorName = "${author.firstName} ${author.lastName}",
                avatarUrl = author.avatarUrl,
                commentText = comment.text,
                publicationDate = mapTimestampToDate(comment.date)
            )
            result.add(postComment)
        }

        return result
    }

    fun mapFaveResponseToPosts(responseDto: FaveResponseDto): List<FeedPost> {
        val result = mutableListOf<FeedPost>()
        val posts = responseDto.faveContentDto.posts
        val groups = responseDto.faveContentDto.groups
        for (post in posts) {
            val group = groups.find { it.id == post.post.communityId.absoluteValue } ?: break
            val feedPost = FeedPost(
                id = post.post.id,
                communityId = post.post.communityId,
                communityName = group.name,
                publicationDate = mapTimestampToDate(post.post.date),
                communityImageUrl = group.imageUrl,
                contentText = post.post.text,
                contentImageUrl = post.post.attachments?.firstOrNull()?.photo?.photoUrls?.lastOrNull()?.url,
                statistics = listOf(
                    StatisticItem(type = StatisticType.LIKES, post.post.likes.count),
                    StatisticItem(type = StatisticType.VIEWS, post.post.views.count),
                    StatisticItem(type = StatisticType.SHARES, post.post.reposts.count),
                    StatisticItem(type = StatisticType.COMMENTS, post.post.comments.count)
                ),
                isLiked = post.post.likes.userLikes > 0
            )
            result.add(feedPost)
        }
        return result
    }

    fun mapUserResponseToUser(responseDto: UserResponseDto): User {
        val user = responseDto.response.first()
        return User(
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            avatar = user.photo,
            city = user.city.title,
            phone = user.phone,
            friends = user.counters.friends,
            followers = user.counters.followers,
            groups = user.counters.groups,
            photos = user.counters.photos,
            videos = user.counters.videos,
            gifts = user.counters.gifts
        )
    }

    private fun mapTimestampToDate(timestamp: Long): String {
        val date = Date(timestamp  * 1000)
        return SimpleDateFormat("d MMMM yyyy, hh:mm", Locale.getDefault()).format(date)
    }
}