package team.seventhmile.tripforp.domain.magazine.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import team.seventhmile.tripforp.domain.magazine.entity.Magazine;
import team.seventhmile.tripforp.domain.magazine.entity.QMagazine;

@RequiredArgsConstructor
@Repository
public class MagazineRepositoryImpl implements MagazineRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	private final QMagazine qMagazine = QMagazine.magazine;

	// 모든 매거진 게시글 조회 (페이징)
	@Override
	public Page<Magazine> getMagazinePosts(Pageable pageable) {
		List<Magazine> magazines = queryFactory.selectFrom(qMagazine)
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(qMagazine.count())
			.from(qMagazine);

		return PageableExecutionUtils.getPage(magazines, pageable, countQuery::fetchOne);
	}

	// 키워드를 통한 매거진 게시글 검색 (제목, 내용 기준)
	@Override
	public Page<Magazine> getMagazineKeywordContaining(String keyword, Pageable pageable) {
		BooleanExpression searchKeyword = qMagazine.title.containsIgnoreCase(keyword)
			.or(qMagazine.content.containsIgnoreCase(keyword));

		List<Magazine> magazines = queryFactory.select(qMagazine)
			.from(qMagazine)
			.where(searchKeyword)
			.limit(pageable.getPageSize())
			.offset(pageable.getOffset())
			.fetch();

		JPAQuery<Long> countQuery = queryFactory
			.select(qMagazine.count())
			.from(qMagazine)
			.where(searchKeyword);

		return PageableExecutionUtils.getPage(magazines, pageable, countQuery::fetchOne);
	}
}
